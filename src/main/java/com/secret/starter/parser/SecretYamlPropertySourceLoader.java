package com.secret.starter.parser;

import com.alibaba.cloud.nacos.parser.AbstractPropertySourceLoader;
import com.secret.starter.kms.AESUtil;
import com.secret.starter.kms.KmsSecret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.*;

public class SecretYamlPropertySourceLoader extends AbstractPropertySourceLoader
        implements Ordered {

    private static final Logger log = LoggerFactory.getLogger(SecretYamlPropertySourceLoader.class);

    /**
     * Get the order value of this object.
     * <p>
     * Higher values are interpreted as lower priority. As a consequence, the object with
     * the lowest value has the highest priority (somewhat analogous to Servlet
     * {@code load-on-startup} values).
     * <p>
     * Same order values will result in arbitrary sort positions for the affected objects.
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] { "yml", "yaml" };
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        if (!ClassUtils.isPresent("org.yaml.snakeyaml.Yaml", getClass().getClassLoader())) {
            throw new IllegalStateException(
                    "Attempted to load " + name + " but snakeyaml was not found on the classpath");
        }
        List<Map<String, Object>> loaded = new OriginTrackedYamlLoader(resource).load();
        if (loaded.isEmpty()) {
            return Collections.emptyList();
        }

        List<PropertySource<?>> propertySources = new ArrayList<>(loaded.size());
        for (int i = 0; i < loaded.size(); i++) {
            String documentNumber = (loaded.size() != 1) ? " (document #" + i + ")" : "";
            Map<String, Object> stringObjectMap = loaded.get(i);
            for (String s : stringObjectMap.keySet()) {
                try {
                    String value = stringObjectMap.get(s).toString();
                    if (value.startsWith("Encrypted:")) {
                        int prefixLength = "Encrypted:".length();
                        String extractedString = value.substring(prefixLength);
//                        AESUtil.decrypt(extractedString)
                        String decrypt = KmsSecret.dncrypt(extractedString);
                        stringObjectMap.put(s, decrypt);
                    }
                } catch (Exception e) {
                    log.error("KmsSecret decrypt failed", e);
                }
            }
            log.info("loaded properties is {}", stringObjectMap);
            propertySources.add(new OriginTrackedMapPropertySource(name + documentNumber,
                    stringObjectMap, true));
        }
        return propertySources;
    }

    @Override
    protected List<PropertySource<?>> doLoad(String name, Resource resource) throws IOException {
        if (!ClassUtils.isPresent("org.yaml.snakeyaml.Yaml", getClass().getClassLoader())) {
            throw new IllegalStateException(
                    "Attempted to load " + name + " but snakeyaml was not found on the classpath");
        }
        List<Map<String, Object>> loaded = new OriginTrackedYamlLoader(resource).load();
        if (loaded.isEmpty()) {
            return Collections.emptyList();
        }
        List<PropertySource<?>> propertySources = new ArrayList<>(loaded.size());
        for (int i = 0; i < loaded.size(); i++) {
            String documentNumber = (loaded.size() != 1) ? " (document #" + i + ")" : "";
            propertySources.add(new OriginTrackedMapPropertySource(name + documentNumber,
                    Collections.unmodifiableMap(loaded.get(i)), true));
        }
        return propertySources;
    }
}
