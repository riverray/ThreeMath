package riversoft


import groovy.json.JsonOutput
import riversoft.base.EndLevelParams
import riversoft.base.FieldParams
import riversoft.base.Game
import riversoft.base.RetModel
import spock.lang.Specification

class GameTests extends Specification {

    def 'получение параметров поля'() {
        given:
        Game game = new Game()
        int level

        when:
        level = 1
        FieldParams param = game.getFieldParams(level)

        then:
        param.width == game.fieldParamsDictionary.fieldParams[level - 1].width
        param.height == game.fieldParamsDictionary.fieldParams[level - 1].height
        param.hidePositions == game.fieldParamsDictionary.fieldParams[level - 1].hidePositions
        param.symbols.size() == 4

        when:
        level = 2
        param = game.getFieldParams(level)

        then:
        param.width == game.fieldParamsDictionary.fieldParams[level - 1].width
        param.height == game.fieldParamsDictionary.fieldParams[level - 1].height
        param.hidePositions == game.fieldParamsDictionary.fieldParams[level - 1].hidePositions
        param.symbols.size() == 5

        when:
        level = 3
        param = game.getFieldParams(level)

        then:
        param.width == game.fieldParamsDictionary.fieldParams[level - 1].width
        param.height == game.fieldParamsDictionary.fieldParams[level - 1].height
        param.hidePositions == game.fieldParamsDictionary.fieldParams[level - 1].hidePositions
        param.symbols.size() == 5

        when:
        level = 33
        param = game.getFieldParams(level)

        then:
        thrown(Exception)
    }

    def 'стартовое поле основные параметры'() {
        given:
        Game game = new Game()
        int level = 1

        when:
        RetModel startField = game.getStartField(level)

        then:
        startField.params.fieldHeight == game.fieldParamsDictionary.fieldParams[level - 1].height
        startField.params.fieldWidth == game.fieldParamsDictionary.fieldParams[level - 1].width
        startField.params.hidePositions == game.fieldParamsDictionary.fieldParams[level - 1].hidePositions
        startField.hod == 0
        startField.level == 1
        startField.totalWin == 0
        startField.currentWin == 0
        startField.endParams.needWin == game.fieldParamsDictionary.fieldParams[0].endParams.needWin
        startField.allFields.size() == 1
        startField.allFields[0].win == 0
        startField.allFields[0].lines.empty
        startField.allFields[0].lines.size() == 0

        when:
        level = 2
        startField = game.getStartField(level)

        then:
        startField.params.fieldHeight == game.fieldParamsDictionary.fieldParams[level - 1].height
        startField.params.fieldWidth == game.fieldParamsDictionary.fieldParams[level - 1].width
        startField.params.hidePositions == game.fieldParamsDictionary.fieldParams[level - 1].hidePositions
        startField.hod == 0
        startField.level == 2
        startField.totalWin == 0
        startField.currentWin == 0
        startField.endParams.needWin == game.fieldParamsDictionary.fieldParams[1].endParams.needWin
        startField.allFields.size() == 1
        startField.allFields[0].win == 0
        startField.allFields[0].lines.empty

        when:
        level = 3
        startField = game.getStartField(level)

        then:
        startField.params.fieldHeight == game.fieldParamsDictionary.fieldParams[level - 1].height
        startField.params.fieldWidth == game.fieldParamsDictionary.fieldParams[level - 1].width
        startField.params.hidePositions == game.fieldParamsDictionary.fieldParams[level - 1].hidePositions
        startField.hod == 0
        startField.level == 3
        startField.totalWin == 0
        startField.currentWin == 0
        startField.endParams.needWin == game.fieldParamsDictionary.fieldParams[2].endParams.needWin
        startField.allFields.size() == 1
        startField.allFields[0].win == 0
        startField.allFields[0].lines.empty
    }

    def 'стартовое поле нет линий'() {
        given:
        Game game = new Game()
        int count = 1000
        RetModel startField
        Random rand = new Random()
        boolean flag = true

        when:
        for (int i = 0; i < count; i++) {
            startField = game.getStartField(rand.nextInt(3) + 1)
            if (startField.allFields.size() > 1 || startField.allFields[0].lines.any()) {
                flag = false
                break
            }
        }

        then:
        flag
    }


    def 'невозможность обмена'() {
        given:
        Game game = new Game()
        int level = 1
        String[][] startMas = [
                ["3", "1", "2", "2", "1", "1"],
                ["2", "3", "4", "3", "1", "1"],
                ["4", "1", "4", "3", "1", "2"],
                ["4", "2", "2", "4", "2", "1"],
                ["2", "2", "3", "4", "1", "3"],
                ["4", "3", "3", "1", "1", "4"]
        ]
        int x1 = 2
        int y1 = 3
        int x2 = 3
        int y2 = 3

        when:
        String[][] tempMas = new int[startMas.length][startMas[0].length]
        cloneField(startMas, tempMas)

        game.getStartField(level)
        game.field.map = tempMas
        def model = game.makeMove(x1, y1, x2, y2)

        then:
        model.totalWin == 0
        model.allFields.size() == 1
        startMas[x1][y1] == model.allFields[0].map[x1 * model.params.fieldWidth + y1]
        startMas[x2][y2] == model.allFields[0].map[x2 * model.params.fieldWidth + y2]
    }

    def 'обмен с формированием одной линии'() {
        given:
        Game game = new Game()
        int level = 1
        String[][] startMas = [
                ["3", "1", "2", "2", "1", "1"],
                ["2", "3", "4", "3", "1", "1"],
                ["4", "1", "4", "3", "4", "4"],
                ["4", "2", "2", "4", "2", "1"],
                ["2", "2", "3", "4", "1", "3"],
                ["4", "3", "3", "1", "1", "4"]
        ]
        int x1 = 2
        int y1 = 3
        int x2 = 3
        int y2 = 3

        when:
        String[][] tempMas = new int[startMas.length][startMas[0].length]
        cloneField(startMas, tempMas)

        game.getStartField(level)
        game.field.map = tempMas
        def model = game.makeMove(x1, y1, x2, y2)

        def s = JsonOutput.toJson(model)

        then:
        model.allFields.size() >= 2
        !model.isEnd
        startMas[x1][y1] != model.allFields[0].map[x1 * model.params.fieldWidth + y1]
        startMas[x2][y2] != model.allFields[0].map[x2 * model.params.fieldWidth + y2]

        startMas[x2][y2] == model.allFields[0].map[x1 * model.params.fieldWidth + y1]
        startMas[x1][y1] == model.allFields[0].map[x2 * model.params.fieldWidth + y2]
    }

    def 'обмен с формированием двух линии'() {
        given:
        Game game = new Game()
        int level = 1
        String[][] startMas = [
                ["3", "1", "2", "4", "1", "1"],
                ["2", "3", "4", "4", "1", "1"],
                ["4", "1", "4", "3", "4", "4"],
                ["4", "2", "2", "4", "2", "1"],
                ["2", "2", "3", "4", "1", "3"],
                ["4", "3", "3", "1", "1", "4"]
        ]
        int x1 = 2
        int y1 = 3

        int x2 = 3
        int y2 = 3

        when:
        String[][] tempMas = new int[startMas.length][startMas[0].length]
        cloneField(startMas, tempMas)

        game.getStartField(level)
        game.field.map = tempMas
        def model = game.makeMove(x1, y1, x2, y2)

        then:
        model.allFields.size() >= 2
        startMas[x1][y1] != model.allFields[0].map[x1 * model.params.fieldWidth + y1]
        startMas[x2][y2] != model.allFields[0].map[x2 * model.params.fieldWidth + y2]

        startMas[x2][y2] == model.allFields[0].map[x1 * model.params.fieldWidth + y1]
        startMas[x1][y1] == model.allFields[0].map[x2 * model.params.fieldWidth + y2]
    }

    def 'проверка на ограничение по количеству ходов'() {
        given:
        Game game = new Game()
        int level = 2
        String[][] startMas = [
                ["3", "1", "2", "2", "1", "1", "3"],
                ["2", "3", "4", "3", "1", "1", "4"],
                ["4", "1", "4", "3", "4", "4", "5"],
                ["4", "2", "2", "4", "2", "1", "2"],
                ["2", "2", "3", "4", "1", "3", "2"],
                ["2", "2", "3", "4", "1", "3", "1"],
                ["4", "3", "3", "1", "1", "4", "1"]
        ]
        int x1 = 2
        int y1 = 3
        int x2 = 3
        int y2 = 3

        when:
        String[][] tempMas = new int[startMas.length][startMas[0].length]
        cloneField(startMas, tempMas)

        game.getStartField(level)
        game.field.map = tempMas
        game.hodLimit = true
        game.currentHodCount = 19
        def model = game.makeMove(x1, y1, x2, y2)

        then:
        model.allFields.size() >= 2
        model.isEnd
    }

    def 'проверка на ограничение по количеству очков'() {
        given:
        Game game = new Game()
        int level = 1
        String[][] startMas = [
                ["3", "1", "2", "2", "1", "1"],
                ["2", "3", "4", "3", "1", "1"],
                ["4", "1", "4", "3", "4", "4"],
                ["4", "2", "2", "4", "2", "1"],
                ["2", "2", "3", "4", "1", "3"],
                ["4", "3", "3", "1", "1", "4"]
        ]
        int x1 = 2
        int y1 = 3
        int x2 = 3
        int y2 = 3

        when:
        String[][] tempMas = new int[startMas.length][startMas[0].length]
        cloneField(startMas, tempMas)

        game.getStartField(level)
        game.field.map = tempMas
        game.currentWin = 29
        def model = game.makeMove(x1, y1, x2, y2)

        then:
        model.allFields.size() >= 2
        model.toNextLevel
    }

    def 'проверка на ограничение по количеству выигрышных линий'() {
        given:
        Game game = new Game()
        int level = 3
        String[][] startMas = [
                ["X", "1", "2", "2", "1", "1", "3", "X"],
                ["2", "3", "4", "3", "1", "1", "4", "2"],
                ["4", "1", "4", "3", "4", "4", "5", "3"],
                ["4", "4", "2", "4", "2", "1", "2", "4"],
                ["2", "2", "3", "4", "1", "3", "2", "5"],
                ["5", "2", "5", "2", "2", "3", "1", "3"],
                ["2", "2", "3", "4", "2", "3", "1", "2"],
                ["X", "3", "3", "1", "1", "4", "5", "X"]
        ]
        int x1 = 3
        int y1 = 1
        int x2 = 2
        int y2 = 1

        when:
        String[][] tempMas = new int[startMas.length][startMas[0].length]
        cloneField(startMas, tempMas)

        game.getStartField(level)
        game.field.map = tempMas
        game.hodLimit = true
        game.currentSymbolLimit = [0, 0, 0, 1, 0]
        def model = game.makeMove(x1, y1, x2, y2)

        then:
        model.allFields.size() >= 2
        model.toNextLevel
    }

    def 'проверка на отрезанные углы'() {
        given:
        Game game = new Game()
        game.fieldParamsDictionary.symbolsByLevels.add([1, 2, 3, 4])
        game.fieldParamsDictionary.fieldParams.add(new FieldParams(width: 5, height: 5, hidePositions: [0, 4, 23, 24], symbols: game.fieldParamsDictionary.getLevelSymbols(game.fieldParamsDictionary.fieldParams.size() + 1).collect(),
                endParams: new EndLevelParams(needWin: 30)))
        int level = 4
        String[][] startMas = [
                ["X", "1", "1", "3", "X"],
                ["2", "2", "3", "4", "1"],
                ["4", "X", "2", "2", "3"],
                ["2", "2", "3", "4", "2"],
                ["X", "3", "4", "4", "X"]
        ]
        int x1 = 2
        int y1 = 2
        int x2 = 3
        int y2 = 2

        when:
        String[][] tempMas = new int[startMas.length][startMas[0].length]
        cloneField(startMas, tempMas)

        game.getStartField(level)
        game.field.map = tempMas
        game.hodLimit = true
        game.currentSymbolLimit = [0, 0, 0, 1, 0]
        def model = game.makeMove(x1, y1, x2, y2)

        then:
        model.allFields.size() >= 2
    }

    def 'запрет обмена несмежных ячеек'() {
        given:
        Game game = new Game()
        int level = 1
        String[][] startMas = [
                ["1", "1", "1", "1", "1", "1"],
                ["1", "1", "1", "1", "1", "1"],
                ["1", "1", "1", "1", "1", "1"],
                ["1", "1", "1", "1", "1", "1"],
                ["1", "1", "1", "1", "1", "1"],
                ["1", "1", "1", "1", "1", "1"]
        ]
        // через строку
        int x1 = 2
        int y1 = 2
        int x2 = 4
        int y2 = 2

        when:
        String[][] tempMas = new int[startMas.length][startMas[0].length]
        cloneField(startMas, tempMas)

        game.getStartField(level)
        game.field.map = tempMas
        def model = game.makeMove(x1, y1, x2, y2)

        then:
        model.totalWin == 0
        model.allFields.size() == 1

        // через столбцы
        when:
        x1 = 2
        y1 = 2
        x2 = 2
        y2 = 5
        model = game.makeMove(x1, y1, x2, y2)

        then:
        model.totalWin == 0
        model.allFields.size() == 1

        // через строки и столбцы
        when:
        x1 = 2
        y1 = 2
        x2 = 4
        y2 = 5
        model = game.makeMove(x1, y1, x2, y2)

        then:
        model.totalWin == 0
        model.allFields.size() == 1

        // по диагонали
        when:
        x1 = 2
        y1 = 2
        x2 = 3
        y2 = 3
        model = game.makeMove(x1, y1, x2, y2)

        then:
        model.totalWin == 0
        model.allFields.size() == 1
    }

    def 'бот уровень 1'() {
        given:
        Game game = new Game()
        int level = 1
        boolean flag = true
        Random rand = new Random()

        when:
        for (int a = 0; a < 1000; a++) {
            RetModel model = game.getStartField(level)

            while (!model.toNextLevel) {
                List<List<String>> mas = []
                for (int i = 0; i < model.params.fieldWidth; i++) {
                    mas.add(model.allFields.last().map.drop(i * model.params.fieldWidth).take(model.params.fieldWidth).toList())
                }
                def potentialList = checkForPotentialLines(mas, model.params.fieldHeight, model.params.fieldWidth)
                def poses = potentialList[rand.nextInt(potentialList.size())]
                model = game.makeMove(poses[0], poses[1], poses[2], poses[3])
            }

        }

        then:
        flag
    }

    def 'бот уровень 2'() {
        given:
        Game game = new Game()
        int level = 2
        boolean flag = true
        Random rand = new Random()

        when:
        for (int a = 0; a < 1000; a++) {
            RetModel model = game.getStartField(level)

            while (!model.toNextLevel) {
                List<List<String>> mas = []
                for (int i = 0; i < model.params.fieldWidth; i++) {
                    mas.add(model.allFields.last().map.drop(i * model.params.fieldWidth).take(model.params.fieldWidth).toList())
                }
                def potentialList = checkForPotentialLines(mas, model.params.fieldHeight, model.params.fieldWidth)
                if (potentialList.size() == 0) {
                    break
                }
                def poses = potentialList[rand.nextInt(potentialList.size())]
                model = game.makeMove(poses[0], poses[1], poses[2], poses[3])

                if (model.isEnd) {
                    break
                }
            }

        }

        then:
        flag
    }

    private void cloneField(String[][] startMas, String[][] tempMas) {
        for (int i = 0; i < startMas.length; i++) {
            for (int j = 0; j < startMas[0].length; j++) {
                tempMas[i][j] = startMas[i][j]
            }
        }
    }

    // проверяем все поле на наличие потенциальных линий
    private List<List<Integer>> checkForPotentialLines(List<List<String>> mas, int height, int width) {
        List<List<Integer>> list = []

        // проверяем два в строке и через одну или сверху/снизу
        for (int i = 0; i < height; i++) {
            String sym = mas[i][0]
            int startPos = 0
            int count = 1

            for (int j = 1; j < width; j++) {
                // символ совпадает - этого достаточно, тогда проверяем
                if (mas[i][j] == sym) {
                    count++
                }
                // символ не совпал
                else {
                    if (count >= 2) {
                        // проверим через один вперед
                        if (j + 1 < width) {
                            if (mas[i][j + 1] == sym) {
                                list.add([i, j, i, j + 1])
                            }
                        }
                        // проверим через один назад
                        if (startPos - 2 >= 0) {
                            if (mas[i][startPos - 2] == sym) {
                                list.add([i, startPos - 1, i, startPos - 2])
                            }
                        }
                        // проверим вверх от символов
                        if (i - 1 >= 0) {
                            if (mas[i - 1][j] == sym) {
                                list.add([i, j, i - 1, j])
                            }
                            if (startPos - 1 >= 0 && mas[i - 1][startPos - 1] == sym) {
                                list.add([i, startPos - 1, i - 1, startPos - 1])
                            }
                        }
                        // проверим вниз от символов
                        if (i + 1 < height) {
                            if (mas[i + 1][j] == sym) {
                                list.add([i, j, i + 1, j])
                            }
                            if (startPos - 1 >= 0 && mas[i + 1][startPos - 1] == sym) {
                                list.add([i, startPos - 1, i + 1, startPos - 1])
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
        for (int j = 0; j < width; j++) {
            String sym = mas[0][j]
            int startPos = 0
            int count = 1

            for (int i = 1; i < height; i++) {
                // символ совпадает - этого достаточно, тогда проверяем
                if (mas[i][j] == sym) {
                    count++
                }
                // символ не совпал
                else {
                    if (count >= 2) {
                        // проверим через один вниз
                        if (i + 1 < height) {
                            if (mas[i + 1][j] == sym) {
                                list.add([i, j, i + 1, j])
                            }
                        }
                        // проверим через один вверх
                        if (startPos - 2 >= 0) {
                            if (mas[startPos - 2][j] == sym) {
                                list.add([startPos - 1, j, startPos - 2, j])
                            }
                        }
                        // проверим влево от символов
                        if (j - 1 >= 0) {
                            if (mas[i][j - 1] == sym) {
                                list.add([i, j, i, j - 1])
                            }
                            if (startPos - 1 >= 0 && mas[startPos - 1][j - 1] == sym) {
                                list.add([startPos - 1, j, startPos - 1, j - 1])
                            }
                        }
                        // проверим вправо от символов
                        if (j + 1 < width) {
                            if (mas[i][j + 1] == sym) {
                                list.add([i, j, i, j + 1])
                            }
                            if (startPos - 1 >= 0 && mas[startPos - 1][j + 1] == sym) {
                                list.add([startPos - 1, j, startPos - 1, j + 1])
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
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width - 2; j++) {
                // есть строка вверху и символ в ней посередине совпадает
                if (i > 0 && mas[i][j] == mas[i][j + 2] && mas[i][j] == mas[i - 1][j + 1]) {
                    list.add([i, j + 1, i - 1, j + 1])
                }
                // есть строка внизу и символ в ней посередине совпадает
                if (i < height - 1 && mas[i][j] == mas[i][j + 2] && mas[i][j] == mas[i + 1][j + 1]) {
                    list.add([i, j + 1, i + 1, j + 1])
                }
            }
        }
        // проверяем через один в столбец и слева/справа
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height - 2; i++) {
                // есть столбец слева и символ в нем посередине совпадает
                if (j > 0 && mas[i][j] == mas[i + 2][j] && mas[i][j] == mas[i + 1][j - 1]) {
                    list.add([i + 1, j, i + 1, j - 1])
                }
                // есть столбец справа и символ в нем посередине совпадает
                if (i < width - 1 && mas[i][j] == mas[i + 2][j] && mas[i][j] == mas[i + 1][j + 1]) {
                    list.add([i, j, i, j + 1])
                }
            }
        }

        return list
    }


}
