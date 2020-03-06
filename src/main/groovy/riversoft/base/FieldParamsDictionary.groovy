package riversoft.base

class FieldParamsDictionary {
    List<FieldParams> fieldParams = []
    List<Symbol> allSymbols = []

    List<List<Integer>> symbolsByLevels = []

    FieldParamsDictionary() {
        // заносим все доступные в игре символы
        allSymbols.add(new Symbol(name: "1"))
        allSymbols.add(new Symbol(name: "2"))
        allSymbols.add(new Symbol(name: "3"))
        allSymbols.add(new Symbol(name: "4"))
        allSymbols.add(new Symbol(name: "5"))

        // задаем номера символов, которые используются уровнем
        symbolsByLevels.add([1, 2, 3, 4])
        symbolsByLevels.add([1, 2, 3, 4, 5])
        symbolsByLevels.add([1, 2, 3, 4, 5])

        // TODO добавить возврат символов
        fieldParams.add(new FieldParams(width: 6, height: 6, hidePositions: [], symbols: getLevelSymbols(fieldParams.size() + 1).collect(),
                endParams: new EndLevelParams(needWin: 30)))
        fieldParams.add(new FieldParams(width: 7, height: 7, hidePositions: [], symbols: getLevelSymbols(fieldParams.size() + 1).collect(),
                endParams: new EndLevelParams(needWin: 50, hodCount: 10)))
        fieldParams.add(new FieldParams(width: 8, height: 8, hidePositions: [0, 7, 56, 63], symbols: getLevelSymbols(fieldParams.size() + 1).collect(),
                endParams: new EndLevelParams(symbolsTypeCount: [0, 2, 2, 0, 2], hodCount: 15)))
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
