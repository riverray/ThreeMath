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
        model.isEnd
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

    private void cloneField(String[][] startMas, String[][] tempMas) {
        for (int i = 0; i < startMas.length; i++) {
            for (int j = 0; j < startMas[0].length; j++) {
                tempMas[i][j] = startMas[i][j]
            }
        }
    }
}
