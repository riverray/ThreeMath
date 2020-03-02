package riversoft.base

class RetModel {
    RetFieldParams params
    int level
    int hod

    int totalWin
    int currentWin
    EndLevelParams endParams

    List<Field> allFields

    boolean isEnd = false


    RetModel() {
        level = -1
    }

    RetModel(List<Field> fields, FieldParams params) {

    }

    RetModel(MainField model) {
        totalWin = model.win
        totalWin = model.totalWin

        this.params = new RetFieldParams(fieldHeight: model.params.height, fieldWidth: model.params.width, hidePositions: model.params.hidePositions.collect())

        for (int i = 0; i < model.params.height; i++) {
            for (int j = 0; j < model.params.width; j++) {
                field.add(model.map[i][j])
            }
        }

        for (def line : model.lines) {
            lines.add(new Line(symbol: line.symbol, count: line.count, positions: line.positions.collect(), win: line.win))
        }


        111
    }
}
