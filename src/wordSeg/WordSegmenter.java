package wordSeg;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface WordSegmenter {

    default public Set<String> seg(String text) {
        return (Set<String>) segMore(text).values().stream().collect(Collectors.toSet());
    }

    public Map<String, String> segMore(String text);
}