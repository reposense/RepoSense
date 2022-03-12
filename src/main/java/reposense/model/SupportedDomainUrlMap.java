package reposense.model;

import reposense.parser.SupportedDomainUrlMapJsonParser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class SupportedDomainUrlMap {

    public static SupportedDomainUrlMap DEFAULT_DOMAIN_URL_MAP;

    private Map<String, Map<String, String>> domainUrlMap;

    public Map<String, Map<String, String>> getDomainUrlMap() {
        return domainUrlMap;
    }

    public boolean isSupportedDomainName(String domainName) {
        return domainUrlMap.containsKey(domainName);
    }

    public static void setupDefaultDomainUrlMap(String pathToJson) throws IOException {
        Path path = Paths.get(pathToJson);
        DEFAULT_DOMAIN_URL_MAP = new SupportedDomainUrlMapJsonParser().parse(path);
    }
}
