package riversoft


import riversoft.base.FieldParams
import riversoft.base.Game
import riversoft.base.MainField
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
        param.fieldWidth == game.fieldParamsDictionary.fieldParams[level - 1].fieldWidth
        param.fieldHeight == game.fieldParamsDictionary.fieldParams[level - 1].fieldHeight
        param.hidePositions == game.fieldParamsDictionary.fieldParams[level - 1].hidePositions
        param.symbols.size() == 4

        when:
        level = 2
        param = game.getFieldParams(level)

        then:
        param.fieldWidth == game.fieldParamsDictionary.fieldParams[level - 1].fieldWidth
        param.fieldHeight == game.fieldParamsDictionary.fieldParams[level - 1].fieldHeight
        param.hidePositions == game.fieldParamsDictionary.fieldParams[level - 1].hidePositions
        param.symbols.size() == 4

        when:
        level = 3
        param = game.getFieldParams(level)

        then:
        param.fieldWidth == game.fieldParamsDictionary.fieldParams[level - 1].fieldWidth
        param.fieldHeight == game.fieldParamsDictionary.fieldParams[level - 1].fieldHeight
        param.hidePositions == game.fieldParamsDictionary.fieldParams[level - 1].hidePositions
        param.symbols.size() == 5

        when:
        level = 33
        param = game.getFieldParams(level)

        then:
        thrown(Exception)
    }

    def 'стартовое поле'() {
        given:
        Game game = new Game()
        int level = 1

        when:
        RetModel startField = game.getStartField(level)

        then:
        startField.params.fieldHeight == game.fieldParamsDictionary.fieldParams[level - 1].fieldHeight
        startField.params.fieldWidth == game.fieldParamsDictionary.fieldParams[level - 1].fieldWidth
        startField.params.hidePositions == game.fieldParamsDictionary.fieldParams[level - 1].hidePositions
        startField.hod == 0
        startField.level == 1
        startField.totalWin == 0
        startField.currentWin == 0
        startField.needWin == game.fieldParamsDictionary.fieldParams[0].needWin
        startField.allFields.size() == 1
        startField.allFields[0].win == 0
        startField.allFields[0].lines.empty

        when:
        level = 2
        startField = game.getStartField(level)

        then:
        startField.params.fieldHeight == game.fieldParamsDictionary.fieldParams[level - 1].fieldHeight
        startField.params.fieldWidth == game.fieldParamsDictionary.fieldParams[level - 1].fieldWidth
        startField.params.hidePositions == game.fieldParamsDictionary.fieldParams[level - 1].hidePositions
        startField.hod == 0
        startField.level == 2
        startField.totalWin == 0
        startField.currentWin == 0
        startField.needWin == game.fieldParamsDictionary.fieldParams[1].needWin
        startField.allFields.size() == 1
        startField.allFields[0].win == 0
        startField.allFields[0].lines.empty

        when:
        level = 3
        startField = game.getStartField(level)

        then:
        startField.params.fieldHeight == game.fieldParamsDictionary.fieldParams[level - 1].fieldHeight
        startField.params.fieldWidth == game.fieldParamsDictionary.fieldParams[level - 1].fieldWidth
        startField.params.hidePositions == game.fieldParamsDictionary.fieldParams[level - 1].hidePositions
        startField.hod == 0
        startField.level == 3
        startField.totalWin == 0
        startField.currentWin == 0
        startField.needWin == game.fieldParamsDictionary.fieldParams[2].needWin
        startField.allFields.size() == 1
        startField.allFields[0].win == 0
        startField.allFields[0].lines.empty
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

        then:
        model.allFields.size() >= 2
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

    private void cloneField(String[][] startMas, String[][] tempMas) {
        for (int i = 0; i < startMas.length; i++) {
            for (int j = 0; j < startMas[0].length; j++) {
                tempMas[i][j] = startMas[i][j]
            }
        }
    }
}
