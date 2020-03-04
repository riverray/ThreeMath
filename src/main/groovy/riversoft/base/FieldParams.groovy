package riversoft.base

class FieldParams {
    int width // ширина поля
    int height // высота поля
    boolean onlyTouchCells = true

    EndLevelParams endParams // параметры для окончания уровня

    List<Symbol> symbols // наборы символов

    List<Integer> hidePositions // неактивные позиции
}
