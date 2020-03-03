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
    boolean toNextLevel = false


    RetModel() {
        level = -1
    }

    RetModel(List<Field> fields, FieldParams params) {
    }
}
