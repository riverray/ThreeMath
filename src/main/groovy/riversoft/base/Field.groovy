package riversoft.base

// набор полей для ответа
class Field {
    String[] map
    List<Line> lines

    List<List<String>> newSymbols = []

    int win

    Field() {
    }

    Field(FieldParams params) {
        map = new String[params.fieldHeight * params.fieldWidth]
    }
}
