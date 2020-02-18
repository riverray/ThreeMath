package riversoft

import riversoft.base.Symbol
import spock.lang.Specification

class SymbolsTests extends Specification {
    def 'проверим формирование символа'() {
        given:
        String name = "1"
        int[] wins = [0, 0, 1, 2, 3]

        when:
        Symbol sym1 = new Symbol(name, wins)

        then:
        sym1.name == name
        sym1.wins == wins

        when:
        Symbol sym2 = new Symbol(name: name, wins: wins)

        then:
        sym2.name == name
        sym2.wins == wins
    }
}
