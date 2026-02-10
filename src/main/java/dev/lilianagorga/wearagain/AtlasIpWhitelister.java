package dev.lilianagorga.wearagain;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@Profile("prod")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AtlasIpWhitelister implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AtlasIpWhitelister.class);

    @Value("${atlas.api.public-key}")
    private String publicKey;

    @Value("${atlas.api.private-key}")
    private String privateKey;

    @Value("${atlas.api.group-id}")
    private String groupId;

    @Override
    public void run(String... args) {
        try {
            String ip = detectPublicIp();
            logger.info("Detected public IP: {}", ip);
            whitelistIp(ip);
            logger.info("Waiting 5 seconds for Atlas propagation...");
            Thread.sleep(5000);
        } catch (Exception e) {
            logger.error("Failed to whitelist IP on Atlas: {}", e.getMessage(), e);
        }
    }

    private String detectPublicIp() throws Exception {
        String[] services = {
                "https://checkip.amazonaws.com",
                "https://api.ipify.org"
        };
        for (String service : services) {
            try {
                URI uri = URI.create(service);
                try (var connection = uri.toURL().openConnection().getInputStream();
                     var reader = new BufferedReader(new InputStreamReader(connection))) {
                    String ip = reader.readLine().trim();
                    if (!ip.isEmpty()) {
                        return ip;
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to get IP from {}: {}", service, e.getMessage());
            }
        }
        throw new RuntimeException("Could not detect public IP from any service");
    }

    private void whitelistIp(String ip) throws Exception {
        String url = "https://cloud.mongodb.com/api/atlas/v2/groups/" + groupId + "/accessList";
        String deleteAfterDate = Instant.now().plus(7, ChronoUnit.DAYS).toString();
        String json = "[{\"ipAddress\":\"" + ip + "\",\"deleteAfterDate\":\"" + deleteAfterDate + "\"}]";

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope("cloud.mongodb.com", 443),
                new UsernamePasswordCredentials(publicKey, privateKey.toCharArray())
        );

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build()) {

            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/vnd.atlas.2023-01-01+json");
            post.setHeader("Accept", "application/vnd.atlas.2023-01-01+json");
            post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

            httpClient.execute(post, response -> {
                int statusCode = response.getCode();
                String body = EntityUtils.toString(response.getEntity());

                if (statusCode == 200 || statusCode == 201) {
                    logger.info("Successfully whitelisted IP {} on Atlas", ip);
                } else if (statusCode == 409) {
                    logger.info("IP {} is already whitelisted on Atlas", ip);
                } else {
                    logger.error("Atlas API returned status {}: {}", statusCode, body);
                }
                return null;
            });
        }
    }
}
