package riversoft.base

class Symbol {
    String name
    int[] wins = []

    Symbol() {
    }

    Symbol(String name, int[] wins) {
        this.name = name
        this.wins = wins.collect()
    }

    Symbol(String name) {
        this.name = name
    }
}
