package riversoft.base

class FieldParamsDictionary {
    List<FieldParams> fieldParams = []
    List<Symbol> allSymbols = []

    int[][] symbolsByLevels

    FieldParamsDictionary() {
        // заносим все доступные в игре символы
        allSymbols.add(new Symbol(name: "1"))
        allSymbols.add(new Symbol(name: "2"))
        allSymbols.add(new Symbol(name: "3"))
        allSymbols.add(new Symbol(name: "4"))
        allSymbols.add(new Symbol(name: "5"))

        // задаем номера символов, которые используются уровнем
        symbolsByLevels = [
                [1, 2, 3, 4],
                [1, 2, 3, 4],
                [1, 2, 3, 4, 5]
        ]

        // TODO добавить возврат символов
        fieldParams.addAll(new FieldParams(fieldWidth: 6, fieldHeight: 6, hidePositions: [], symbols: getLevelSymbols(fieldParams.size() + 1).collect(), needWin: 50))
        fieldParams.addAll(new FieldParams(fieldWidth: 7, fieldHeight: 7, hidePositions: [], symbols: getLevelSymbols(fieldParams.size() + 1).collect(), needWin: 100))
        fieldParams.addAll(new FieldParams(fieldWidth: 8, fieldHeight: 8, hidePositions: [], symbols: getLevelSymbols(fieldParams.size() + 1).collect(), needWin: 150))
        111
    }

    List<Symbol> getLevelSymbols(int level) {
        List<Symbol> list = []
        for (int i = 0; i < symbolsByLevels[level - 1].size(); i++) {
            list.add(allSymbols[i])
        }

        return list
    }

    FieldParams getLevelParams(int level) {
        if (level > fieldParams.size() || level <= 0) {
            throw new Exception('Incorrect level')
        }
        return fieldParams[--level]
    }


}
