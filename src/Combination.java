import java.util.ArrayList;

// Java program to print all combination of size r in an array of size n
public class Combination {


    ArrayList<int[]> combinations;


    public Combination (int numberOfSets) {

        this.combinations = new ArrayList<>();

        int arr[] = new int[numberOfSets];
        for (int i = 0 ; i < numberOfSets ; i++) {
            arr[i] = i;
        }
        int r = 3;
        int n = arr.length;
        findCombinations(arr, n, r);
    }


    /* arr[] ---> Input Array
    data[] ---> Temporary array to store current combination
    start & end ---> Staring and Ending indexes in arr[]
    index ---> Current index in data[]
    r ---> Size of a combination to be printed */
    void combinationUtil (int arr[], int data[], int start, int end,
     int index, int r) {
        // Current combination is ready to be printed, print it
        if (index == r) {
            combinations.add(data);
        }

        // replace index with all possible elements. The condition
        // "end-i+1 >= r-index" makes sure that including one element
        // at index will make a combination with remaining elements
        // at remaining positions
        for (int i = start ; i <= end && end - i + 1 >= r - index ; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i + 1, end, index + 1, r);
        }
    }


    // The main function that prints all combinations of size r
    // in arr[] of size n. This function mainly uses combinationUtil()
    void findCombinations (int arr[], int n, int r) {
        // A temporary array to store all combination one by one
        int data[] = new int[r];

        // Print all combination using temporary array 'data[]'
        combinationUtil(arr, data, 0, n - 1, 0, r);
    }


}

/* This code is contributed by Devesh Agrawal */
