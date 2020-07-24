package phonebook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PhoneBook {
    private static long linearSearchDuration;
    private static long bubbleSearchDuration;

    public static void main(String[] args) throws IOException {
        String[] directory = Files.readString(Path.of("/home/yousef/Downloads/directory.txt")).split("\\r\\n");
        for (int i = 0; i < directory.length; i++) {
            directory[i] = directory[i].replaceAll("\\d+\\s?", "");
        }
        String[] find = Files.readString(Path.of("/home/yousef/Downloads/find.txt")).split("\\r\\n");
        linearSearch(directory, find);
        bubbleJumpSearch(directory, find);
        quickSortBinarySearch(directory, find);
        hasTableSearch(directory, find);
    }

    private static void hasTableSearch(String[] directory, String[] find) {
        System.out.println("\n\nStart searching (hash table) ...");
        long hashTableDuration = System.currentTimeMillis();
        Map<String, Integer> map = new HashMap<>(directory.length);
        for (String s : directory) {
            map.put(s, 0);
        }
        hashTableDuration = System.currentTimeMillis() - hashTableDuration;
        long millis = System.currentTimeMillis();
        int counter = 0;
        for (String s : find) {
            if (map.get(s) != null) {
                counter++;
            }
        }
        millis = System.currentTimeMillis() - millis;
        System.out.printf("Found %d / %d entries. Time taken: %s", counter, find.length, getTimeTaken(millis + hashTableDuration));
        System.out.printf("\nCreating time: %s", getTimeTaken(hashTableDuration));
        System.out.printf("\nSearching time: %s", getTimeTaken(millis));
    }

    private static void linearSearch(String[] directory, String[] find) {
        System.out.println("Start searching (linear search) ...");
        linearSearchDuration = System.currentTimeMillis();
        int counter = 0;
        counter = doLinearSearch(directory, find, counter);
        linearSearchDuration = System.currentTimeMillis() - linearSearchDuration;
        System.out.printf("Found %d / %d entries. Time taken: %s", counter, find.length, getTimeTaken(linearSearchDuration));
    }

    private static int doLinearSearch(String[] directory, String[] find, int counter) {
        for (String s : find) {
            for (String s1 : directory) {
                if (s1.contains(s)) {
                    counter++;
                    break;
                }
            }
        }
        return counter;
    }

    private static int binarySearch(String[] directory, String[] find) {
        int counter = 0;
        for (String s : find) {
            int left = 0;
            int right = directory.length - 1;
            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (s.equals(directory[mid])) {
                    counter++;
                    break;
                } else if (s.compareTo(directory[mid]) < 0) right = mid - 1;
                else left = mid + 1;
            }
        }
        return counter;
    }

    private static void bubbleJumpSearch(String[] directory, String[] find) throws IOException {
        System.out.println("\n\nStart searching (bubble sort + jump search) ...");
        if (!bubbleSortByName(directory)) {
            long millis = System.currentTimeMillis();
            int counter = 0;
            counter = doLinearSearch(directory, find, counter);
            millis = System.currentTimeMillis() - millis;
            System.out.printf("Found %d / %d entries. Time taken: %s", counter, find.length, getTimeTaken(millis + bubbleSearchDuration));
            System.out.printf("\nSorting time: %s - STOPPED, moved to linear search", getTimeTaken(bubbleSearchDuration));
            System.out.printf("\nSearching time: %s", getTimeTaken(millis));
        }
    }

    private static void quickSortBinarySearch(String[] directory, String[] find) {
        System.out.println("\n\nStart searching (quick sort + binary search) ...");
        long quickSortDuration = System.currentTimeMillis();
        quickSort(directory, 0, directory.length - 1);
        quickSortDuration = System.currentTimeMillis() - quickSortDuration;
        long millis = System.currentTimeMillis();
        int counter;
        counter = binarySearch(directory, find);
        millis = System.currentTimeMillis() - millis;
        System.out.printf("Found %d / %d entries. Time taken: %s", counter, find.length, getTimeTaken(millis + quickSortDuration));
        System.out.printf("\nSorting time: %s", getTimeTaken(quickSortDuration));
        System.out.printf("\nSearching time: %s", getTimeTaken(millis));
    }


    private static boolean bubbleSortByName(String[] array) throws IOException {
        long millis = System.currentTimeMillis();
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j].compareTo(array[j + 1]) > 0) {
                    String temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    bubbleSearchDuration = System.currentTimeMillis() - millis;
                    if (bubbleSearchDuration > (10 * linearSearchDuration)) {
                        return false;
                    }
                }
            }
        }
        System.out.println();
        Files.write(Path.of("/home/yousef/Downloads/directory-sorted.txt"), Arrays.stream(array).collect(Collectors.toList()));
        return true;
    }

    private static String getTimeTaken(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes);
        millis = millis - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.SECONDS.toMillis(seconds);
        return String.format("%d min. %d sec. %d ms.", minutes, seconds, millis);
    }

    private static void quickSort(String[] array, int left, int right) {

        if (left < right) {
            int pivotIndex = partition(array, left, right);
            quickSort(array, left, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, right);
        }
    }

    private static int partition(String[] array, int left, int right) {
        String pivot = array[right];
        int partitionIndex = left;

        for (int i = left; i < right; i++) {
            if (array[i].compareTo(pivot) <= 0) {
                swap(array, i, partitionIndex);
                partitionIndex++;
            }
        }

        swap(array, partitionIndex, right);

        return partitionIndex;
    }

    private static void swap(String[] array, int i, int j) {
        String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
