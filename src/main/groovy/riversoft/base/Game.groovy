package riversoft.base

class Game {
    // параметра поля
    FieldParams params

    // номер уровня
    int level

    // количество ходов
    int hod

    // список параметров уровней
    FieldParamsDictionary fieldParamsDictionary = new FieldParamsDictionary()

    // рабочее поле
    MainField field

    // набор полей для возврата
    List<Field> allFields = []

    // текущий выигрыш
    int currentWin

    Random rand = new Random()

    Game() {
        level = -1
    }

    FieldParams getFieldParams(int level) {
        return fieldParamsDictionary.getLevelParams(level)
    }

    RetModel getStartField(int level) {
        hod = currentWin = 0
        allFields.clear()
        this.level = level
        params = getFieldParams(level)
        field = new MainField(params)

        // заполнение символами
        for (int i = 0; i < params.fieldWidth; i++) {
            for (int j = 0; j < params.fieldHeight; j++) {
                field.map[i][j] = params.symbols[rand.nextInt(params.symbols.size())].name
            }
        }

        allFields.add(new Field(win: 0, map: convertTwoMasToOneMas(field.map), lines: []))

        return new RetModel(
                level: level,
                hod: hod,
                totalWin: 0,
                currentWin: 0,
                needWin: params.needWin,
                params: getCurrentGameParams(),
                allFields: allFields.collect()
        )
    }

    RetModel makeMove(int posX1, int posY1, int posX2, int posY2) {
        if (posX1 >= params.fieldWidth || posX2 >= params.fieldWidth || posY1 >= params.fieldHeight || posY2 >= params.fieldHeight ||
                posX1 < 0 || posY1 < 0 || posX2 < 0 || posY2 < 0) {
            throw new RuntimeException("Cells not in field")
        }

        allFields.clear()

        int x1 = posX1
        int y1 = posY1
        int x2 = posX2
        int y2 = posY2

        def mas = field.map
        // меняем символы местами
        swapSymbols(mas, x1, y1, x2, y2)

        // ищем линии после обмена
        tryFindLinesAfterSwap(mas, x1, y1, x2, y2)

        // если не появляется линий - обратный обмен и возврат нулевого выигрыша
        if (field.lines.size() == 0) {
            swapSymbols(mas, x1, y1, x2, y2)

            allFields.add(new Field(win: 0, map: convertTwoMasToOneMas(field.map), lines: []))

            return new RetModel(
                    level: level,
                    hod: hod,
                    totalWin: 0,
                    currentWin: 0,
                    needWin: params.needWin,
                    params: getCurrentGameParams(),
                    allFields: allFields.collect()
            )
        }

        hod++
        allFields.add(new Field(win: field.lines.sum { t -> t.win }, map: convertTwoMasToOneMas(field.map), lines: field.lines.collect()))

        // если линии появляются - считаем данный ход
        boolean isContinue = true

        while (isContinue) {
            // заменяем символы на ноль
            for (def line : field.lines) {
                for (def pos : line.positions) {
                    mas[pos[0]][pos[1]] = "0"
                }
            }
            field.lines.clear()

            // опускаем символы на низ матрицы
            for (int j = 0; j < params.fieldWidth; j++) {
                // проверяем наличие нулевых символов
                int count = 0
                List<String> tempMas = []
                for (int i = params.fieldHeight - 1; i >= 0; i--) {
                    if (/*mas[i][j] != "x" &&*/ mas[i][j] != "0") {
                        count++
                        tempMas.add(mas[i][j])
                    }
                }
                // если нет нулевых символов - ничего не делаем
                if (count == params.fieldHeight) {
                    continue
                }
                // если же есть - пробуем опускать
                int pos = 0
                for (int i = params.fieldHeight - 1; i >= 0; i--) {
                    if (pos < tempMas.size()) {
                        mas[i][j] = tempMas[pos++]
                    }
                    else {
                        mas[i][j] = "0"
                    }
                }
            }

            // досыпаем символы
            for (int j = 0; j < params.fieldWidth; j++) {
                for (int i = 0; i < params.fieldHeight; i++) {
                    if (field.map[i][j] == "0") {
                        field.map[i][j] = params.symbols[rand.nextInt(params.symbols.size())].name
                    }
                }
            }

            // проверяем на все возможные линии
            checkForLines(mas)

            // заносим поле
            allFields.add(new Field(win: (field.lines.any() ? field.lines.sum { t -> t.win } : 0), map: convertTwoMasToOneMas(field.map), lines: field.lines.collect()))

            // если есть линии - заносим и продолжаем
            if (!field.lines.any()) {
                isContinue = false
            }
        }

        int totalWin = allFields.sum { t -> t.win }
        currentWin += totalWin
        return new RetModel(
                level: level,
                hod: hod,
                totalWin: totalWin,
                currentWin: currentWin,
                needWin: params.needWin,
                params: getCurrentGameParams(),
                allFields: allFields.collect()
        )

    }

    private void tryFindLinesAfterSwap(String[][] mas, int x1, int y1, int x2, int y2) {
        // определяем символ первой позиции
        String oneSymbol = mas[x1][y1]

        checkRowAndColumnForLines(mas, x1, y1, oneSymbol)

        // определяем символ второй позиции
        String twoSymbol = mas[x2][y2]

        checkRowAndColumnForLines(mas, x2, y2, twoSymbol)
    }

    private RetFieldParams getCurrentGameParams() {
        new RetFieldParams(fieldWidth: params.fieldWidth, fieldHeight: params.fieldHeight, hidePositions: params.hidePositions.collect())
    }

    // обмен местами символов в массиве
    private void swapSymbols(String[][] mas, int x1, int y1, int x2, int y2) {
        def temp = mas[x1][y1]
        mas[x1][y1] = mas[x2][y2]
        mas[x2][y2] = temp
    }

    // проверяем получились ли линии из трех или более относительно заданной позиции
    private void checkRowAndColumnForLines(String[][] mas, int x, int y, String symbol) {
        // проверяем ряд второй позиции с новым символом
        int count = 0

        List<List<Integer>> poses = []
        List<Integer> indexes = []
        for (int j = 0; j < params.fieldWidth; j++) {
            if (j == y || mas[x][j] == symbol) {
                count++
                poses.add([x, j])
                indexes.add(x * params.fieldWidth + j)
            }
            else {
                if (poses.any { t -> t[1] == y }) {
                    break
                }
                count = 0
                poses.clear()
                indexes.clear()
            }
        }
        if (poses.size() >= 3) {
            field.lines.add(new Line(symbol: symbol, count: count, positions: poses.collect(), indexes: indexes.collect(), win: count))
        }

        // проверяем столбец второй позиции с новым символом
        count = 0
        poses = []
        indexes = []
        for (int i = 0; i < params.fieldWidth; i++) {
            if (i == x || mas[i][y] == symbol) {
                count++
                poses.add([i, y])
                indexes.add(i * params.fieldWidth + y)
            }
            else {
                if (poses.any { t -> t[0] == x }) {
                    break
                }
                count = 0
                poses.clear()
                indexes.clear()
            }
        }
        if (poses.size() >= 3) {
            field.lines.add(new Line(symbol: symbol, count: count, positions: poses.collect(), indexes: indexes.collect(), win: count))
        }

        111
    }

    // проверяем все поле на получение линий
    private void checkForLines(String[][] mas) {
        // проверяем строки на линии
        List<List<Integer>> poses = []
        List<Integer> indexes = []
        for (int i = 0; i < params.fieldHeight; i++) {
            poses.clear()
            indexes.clear()
            String sym = mas[i][0]
            int count = 1
            poses.add([i, 0])
            indexes.add(i * params.fieldWidth)

            for (int j = 1; j < params.fieldWidth; j++) {
                // символ совпадает - продолжаем считать
                if (mas[i][j] == sym) {
                    count++
                    poses.add([i, j])
                    indexes.add(i * params.fieldWidth + j)
                }
                // символ не совпал
                else {
                    // если из предыдущего символа уже собралась линия - сохраняем ее
                    if (count >= 3) {
                        field.lines.add(new Line(symbol: sym, count: count, positions: poses.collect(), indexes: indexes.collect(), win: count))
                    }
                    // меняем символ и начинаем считать дальше
                    poses.clear()
                    indexes.clear()
                    sym = mas[i][j]
                    count = 1
                    poses.add([i, j])
                    indexes.add(i * params.fieldWidth + j)
                }
            }

            if (count >= 3) {
                field.lines.add(new Line(symbol: sym, count: count, positions: poses.collect(), indexes: indexes.collect(), win: count))
            }
        }


        // проверяем столбцы на линии
        poses = []
        indexes = []
        for (int j = 0; j < params.fieldWidth; j++) {
            poses.clear()
            indexes.clear()
            String sym = mas[0][j]
            int count = 1
            poses.add([0, j])
            indexes.add(j)

            for (int i = 1; i < params.fieldHeight; i++) {
                // символ совпадает - продолжаем считать
                if (mas[i][j] == sym) {
                    count++
                    poses.add([i, j])
                    indexes.add(i * params.fieldWidth + j)
                }
                // символ не совпал
                else {
                    // если из предыдущего символа уже собралась линия - сохраняем ее
                    if (count >= 3) {
                        field.lines.add(new Line(symbol: sym, count: count, positions: poses.collect(), indexes: indexes.collect(), win: count))
                    }
                    // меняем символ и начинаем считать дальше
                    poses.clear()
                    indexes.clear()
                    sym = mas[i][j]
                    count = 1
                    poses.add([i, j])
                    indexes.add(i * params.fieldWidth + j)
                }
            }

            if (count >= 3) {
                field.lines.add(new Line(symbol: sym, count: count, positions: poses.collect(), indexes: indexes.collect(), win: count))
            }
        }
    }

    String[] convertTwoMasToOneMas(String[][] mas) {
        String[] newMas = new String[mas.length * mas[0].length]
        int pos = 0
        for (int i = 0; i < mas.length; i++) {
            for (int j = 0; j < mas[0].length; j++) {
                newMas[pos++] = mas[i][j]
            }
        }

        return newMas
    }


}
