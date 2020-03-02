package riversoft.base

class FieldParams {
    int width // ширина поля
    int height // высота поля

    EndLevelParams endParams // параметры для окончания уровня

//    int needWin // необходимое количество очков
//    int hodCount // ограничение на количество ходов

    List<Symbol> symbols // наборы символов

    List<Integer> hidePositions // неактивные позиции
}
