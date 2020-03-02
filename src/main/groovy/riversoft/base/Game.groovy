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

    boolean hodLimit = false
    int currentHodCount = 0

    Random rand = new Random()

    Game() {
        level = -1
    }

    FieldParams getFieldParams(int level) {
        return fieldParamsDictionary.getLevelParams(level)
    }

    RetModel getStartField(int level) {
        hod = currentWin = 0
        this.level = level
        params = getFieldParams(level)

        // устанавливаем ходлимит
        if (params.endParams.hodCount > 0) {
            hodLimit = true
        }

        allFields.clear()
        field = new MainField(params)

        boolean correctMatrix = true

        while (correctMatrix) {
            // заполнение символами
            for (int i = 0; i < params.width; i++) {
                for (int j = 0; j < params.height; j++) {
                    field.map[i][j] = params.symbols[rand.nextInt(params.symbols.size())].name
                }
            }

            // проверка на наличие линий
            def mas = field.map
            checkForLines(mas)

            // пока есть линии - не выходим
            while (field.lines.any()) {
                // пробуем заменить часть символов и проверить на линии
                for (int i = 0; i < field.lines.size(); i++) {
                    int number = rand.nextInt(field.lines[i].count)
                    int x = field.lines[i].positions[number][0]
                    int y = field.lines[i].positions[number][1]
                    String sym = params.symbols[rand.nextInt(params.symbols.size())].name
                    while (sym == mas[x][y]) {
                        sym = params.symbols[rand.nextInt(params.symbols.size())].name
                    }
                    mas[x][y] = sym

                }

                field.lines.clear()

                checkForLines(mas)
                111
            }

            // должно быть потенциально хотя бы одна линия
            correctMatrix = !checkForPotentialLines(mas)
        }

        allFields.add(new Field(win: 0, map: convertTwoMasToOneMas(field.map), lines: field.lines.collect()))

        return new RetModel(
                level: level,
                hod: hod,
                totalWin: 0,
                currentWin: 0,
                endParams: params.endParams,
                params: getCurrentGameParams(),
                allFields: allFields.collect()
        )
    }

    RetModel makeMove(int posX1, int posY1, int posX2, int posY2) {
        if (posX1 >= params.width || posX2 >= params.width || posY1 >= params.height || posY2 >= params.height ||
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
                    endParams: params.endParams,
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

            List<List<String>> newSymbols = []

            // опускаем символы на низ матрицы
            for (int j = 0; j < params.width; j++) {
                // проверяем наличие нулевых символов
                int count = 0
                List<String> tempMas = []
                for (int i = params.height - 1; i >= 0; i--) {
                    if (/*mas[i][j] != "x" &&*/ mas[i][j] != "0") {
                        count++
                        tempMas.add(mas[i][j])
                    }
                }
                // если нет нулевых символов - ничего не делаем
                if (count == params.height) {
                    continue
                }
                // если же есть - пробуем опускать
                int pos = 0
                for (int i = params.height - 1; i >= 0; i--) {
                    if (pos < tempMas.size()) {
                        mas[i][j] = tempMas[pos++]
                    }
                    else {
                        mas[i][j] = "0"
                    }
                }
//                return newSymbols
            }

            // досыпаем символы
            for (int j = 0; j < params.width; j++) {
                List<String> tempCol = []
                newSymbols.add([])
                for (int i = 0; i < params.height; i++) {
                    if (field.map[i][j] == "0") {
                        field.map[i][j] = params.symbols[rand.nextInt(params.symbols.size())].name
                        newSymbols[j].add(field.map[i][j])
                        tempCol.add(field.map[i][j])
                    }
                }
            }

            // проверяем на все возможные линии
            checkForLines(mas)

            // заносим поле
            allFields.add(new Field(
                    win: (field.lines.any() ? field.lines.sum { t -> t.win } : 0),
                    map: convertTwoMasToOneMas(field.map),
                    newSymbols: newSymbols.collect(),
                    lines: field.lines.collect()))

            // если есть линии - заносим и продолжаем
            if (!field.lines.any()) {
                isContinue = false
            }
        }

        // меняем местами позиции в массиве
        for (int i = 0; i < allFields.size(); i++) {
            if (allFields[i].lines.any()) {
                for (int j = 0; j < allFields[i].lines.size(); j++) {
                    for (int a = 0; a < allFields[i].lines[j].positions.size(); a++) {
                        int temp = allFields[i].lines[j].positions[a][0]
                        allFields[i].lines[j].positions[a][0] = allFields[i].lines[j].positions[a][1]
                        allFields[i].lines[j].positions[a][1] = temp
                    }
                }
            }
        }

        boolean isEnd = false
        if (hodLimit) {
            currentHodCount++

            if (currentHodCount == params.endParams.hodCount) {
                isEnd = true
            }

        }

        int totalWin = allFields.sum { t -> t.win }
        currentWin += totalWin
        return new RetModel(
                level: level,
                hod: hod,
                totalWin: totalWin,
                currentWin: currentWin,
                endParams: params.endParams,
                params: getCurrentGameParams(),
                allFields: allFields.collect(),
                isEnd: isEnd
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
        new RetFieldParams(fieldWidth: params.width, fieldHeight: params.height, hidePositions: params.hidePositions.collect())
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
        for (int j = 0; j < params.width; j++) {
            if (j == y || mas[x][j] == symbol) {
                count++
                poses.add([x, j])
                indexes.add(x * params.width + j)
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
            field.lines.add(new Line(symbol: symbol, count: count, positions: poses.collect(), win: count))
        }

        // проверяем столбец второй позиции с новым символом
        count = 0
        poses = []
        indexes = []
        for (int i = 0; i < params.width; i++) {
            if (i == x || mas[i][y] == symbol) {
                count++
                poses.add([i, y])
                indexes.add(i * params.width + y)
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
            field.lines.add(new Line(symbol: symbol, count: count, positions: poses.collect(), win: count))
        }

        111
    }

    // проверяем все поле на получение линий
    private void checkForLines(String[][] mas) {
        // проверяем строки на линии
        List<List<Integer>> poses = []
        List<Integer> indexes = []
        for (int i = 0; i < params.height; i++) {
            poses.clear()
            indexes.clear()
            String sym = mas[i][0]
            int count = 1
            poses.add([i, 0])
            indexes.add(i * params.width)

            for (int j = 1; j < params.width; j++) {
                // символ совпадает - продолжаем считать
                if (mas[i][j] == sym) {
                    count++
                    poses.add([i, j])
                    indexes.add(i * params.width + j)
                }
                // символ не совпал
                else {
                    // если из предыдущего символа уже собралась линия - сохраняем ее
                    if (count >= 3) {
                        field.lines.add(new Line(symbol: sym, count: count, positions: poses.collect(), win: count))
                    }
                    // меняем символ и начинаем считать дальше
                    poses.clear()
                    indexes.clear()
                    sym = mas[i][j]
                    count = 1
                    poses.add([i, j])
                    indexes.add(i * params.width + j)
                }
            }

            if (count >= 3) {
                field.lines.add(new Line(symbol: sym, count: count, positions: poses.collect(), win: count))
            }
        }

        // проверяем столбцы на линии
        poses = []
        indexes = []
        for (int j = 0; j < params.width; j++) {
            poses.clear()
            indexes.clear()
            String sym = mas[0][j]
            int count = 1
            poses.add([0, j])
            indexes.add(j)

            for (int i = 1; i < params.height; i++) {
                // символ совпадает - продолжаем считать
                if (mas[i][j] == sym) {
                    count++
                    poses.add([i, j])
                    indexes.add(i * params.width + j)
                }
                // символ не совпал
                else {
                    // если из предыдущего символа уже собралась линия - сохраняем ее
                    if (count >= 3) {
                        field.lines.add(new Line(symbol: sym, count: count, positions: poses.collect(), win: count))
                    }
                    // меняем символ и начинаем считать дальше
                    poses.clear()
                    indexes.clear()
                    sym = mas[i][j]
                    count = 1
                    poses.add([i, j])
                    indexes.add(i * params.width + j)
                }
            }

            if (count >= 3) {
                field.lines.add(new Line(symbol: sym, count: count, positions: poses.collect(), win: count))
            }
        }
    }

    // проверяем все поле на наличие потенциальных линий
    private boolean checkForPotentialLines(String[][] mas) {
        // проверяем два в строке и через одну или сверху/снизу
        for (int i = 0; i < params.height; i++) {
            String sym = mas[i][0]
            int startPos = 0
            int count = 1

            for (int j = 1; j < params.width; j++) {
                // символ совпадает - этого достаточно, тогда проверяем
                if (mas[i][j] == sym) {
                    count++
                }
                // символ не совпал
                else {
                    if (count >= 2) {
                        // проверим через один вперед
                        if (j + 1 < params.width) {
                            if (mas[i][j + 1] == sym) {
                                return true
                            }
                        }
                        // проверим через один назад
                        if (startPos - 2 >= 0) {
                            if (mas[i][startPos - 2] == sym) {
                                return true
                            }
                        }
                        // проверим вверх от символов
                        if (i - 1 >= 0) {
                            if (mas[i - 1][j] == sym) {
                                return true
                            }
                            if (startPos - 1 >= 0 && mas[i - 1][startPos - 1] == sym) {
                                return true
                            }
                        }
                        // проверим вниз от символов
                        if (i + 1 < params.height) {
                            if (mas[i + 1][j] == sym) {
                                return true
                            }
                            if (startPos - 1 >= 0 && mas[i + 1][startPos - 1] == sym) {
                                return true
                            }
                        }
                    }
                    sym = mas[i][j]
                    count = 1
                    startPos = j
                }
            }
        }

        // проверяем два в ряду и через одну или сбоку
        for (int j = 0; j < params.width; j++) {
            String sym = mas[0][j]
            int startPos = 0
            int count = 1

            for (int i = 1; i < params.height; i++) {
                // символ совпадает - этого достаточно, тогда проверяем
                if (mas[i][j] == sym) {
                    count++
                }
                // символ не совпал
                else {
                    if (count >= 2) {
                        // проверим через один вниз
                        if (i + 1 < params.height) {
                            if (mas[i + 1][j] == sym) {
                                return true
                            }
                        }
                        // проверим через один вверх
                        if (startPos - 2 >= 0) {
                            if (mas[startPos - 2][j] == sym) {
                                return true
                            }
                        }
                        // проверим влево от символов
                        if (j - 1 >= 0) {
                            if (mas[i][j - 1] == sym) {
                                return true
                            }
                            if (startPos - 1 >= 0 && mas[startPos - 1][j - 1] == sym) {
                                return true
                            }
                        }
                        // проверим вправо от символов
                        if (j + 1 < params.width) {
                            if (mas[i][j + 1] == sym) {
                                return true
                            }
                            if (startPos - 1 >= 0 && mas[startPos - 1][j + 1] == sym) {
                                return true
                            }
                        }
                    }
                    sym = mas[i][j]
                    count = 1
                    startPos = i
                }
            }
        }

        // проверяем через один в строку и сверху/снизу
        for (int i = 0; i < params.height; i++) {
            for (int j = 0; j < params.width - 2; j++) {
                // есть строка вверху и символ в ней посередине совпадает
                if (i > 0 && mas[i][j] == mas[i][j + 2] && mas[i][j] == mas[i - 1][j + 1]) {
                    return true
                }
                // есть строка внизу и символ в ней посередине совпадает
                if (i < params.height - 1 && mas[i][j] == mas[i][j + 2] && mas[i][j] == mas[i + 1][j + 1]) {
                    return true
                }
            }
        }
        // проверяем через один в столбец и слева/справа
        for (int j = 0; j < params.width; j++) {
            for (int i = 0; i < params.height - 2; i++) {
                // есть столбец слева и символ в нем посередине совпадает
                if (j > 0 && mas[i][j] == mas[i + 2][j] && mas[i][j] == mas[i + 1][j - 1]) {
                    return true
                }
                // есть столбец справа и символ в нем посередине совпадает
                if (i < params.width - 1 && mas[i][j] == mas[i + 2][j] && mas[i][j] == mas[i + 1][j + 1]) {
                    return true
                }
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
