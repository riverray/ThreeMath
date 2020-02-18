package riversoft.base

// набор полей для ответа
class Field {
    String[] map
    List<Line> lines

    int win

    Field() {
    }

    Field(FieldParams params) {
        map = new String[params.fieldHeight * params.fieldWidth]
    }
}
