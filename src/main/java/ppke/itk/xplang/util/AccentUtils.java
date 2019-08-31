package ppke.itk.xplang.util;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;

public class AccentUtils {
    private final static Map<Character, List<Character>> accentMapping = new HashMap<>();
    static {
        accentMapping.put('á', asList('a', 'á'));
        accentMapping.put('é', asList('e', 'é'));
        accentMapping.put('í', asList('i', 'í'));
        accentMapping.put('ó', asList('o', 'ó'));
        accentMapping.put('ö', asList('o', 'ö'));
        accentMapping.put('ő', asList('o', 'õ', 'ô', 'ő'));
        accentMapping.put('ű', asList('u', 'ũ', 'û', 'ű'));
        accentMapping.put('ü', asList('u', 'ü'));
    }

    private AccentUtils() {}

    public static Set<String> calculateVariants(String string) {
        char[] arr = string.toCharArray();

        List<Integer> positions = new ArrayList<>();
        List<List<Character>> lists = new ArrayList<>();
        for (int i = 0; i < arr.length; ++i) {
            if (accentMapping.containsKey(arr[i])) {
                positions.add(i);
                lists.add(accentMapping.get(arr[i]));
            }
        }

        List<List<Character>> variations = Combinations.cartesianProduct(lists);
        Set<String> result = new HashSet<>();
        for (List<Character> variation : variations) {
            char[] n = copyOf(arr, arr.length);
            for (int i = 0; i < positions.size(); ++i) {
                n[positions.get(i)] = variation.get(i);
            }
            result.add(new String(n));
        }

        return result;
    }
}
