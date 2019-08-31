package ppke.itk.xplang.util;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class Combinations {
    private Combinations() {}

    public static <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        if (lists.isEmpty()) {
            return singletonList(emptyList());
        }

        List<List<T>> result = new ArrayList<>();
        for (T element : lists.get(0)) {
            ArrayList<T> instance = new ArrayList<>();
            instance.add(element);
            result.add(instance);
        }

        for (int i = 1; i < lists.size(); ++i) {
            List<List<T>> next = new ArrayList<>();
            for (List<T> candidate : result) {
                for (T character : lists.get(i)) {
                    List<T> instance = new ArrayList<>(candidate);
                    instance.add(character);
                    next.add(instance);
                }
            }
            result = next;
        }

        return result;
    }
}
