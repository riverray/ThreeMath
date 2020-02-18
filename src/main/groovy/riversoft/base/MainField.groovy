package riversoft.base

class MainField {
    String[][] map
    List<Line> lines = []

    int win
    int totalWin

    MainField() {
    }

    MainField(FieldParams params) {
        map = new String[params.fieldHeight][params.fieldWidth]
    }
}
