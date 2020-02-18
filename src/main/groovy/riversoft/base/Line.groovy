package riversoft.base

import groovy.transform.ToString

@ToString (includeNames = true)
class Line {
    String symbol
    int count
    int win
    List<List<Integer>> positions = []
    List<Integer> indexes = []
}
