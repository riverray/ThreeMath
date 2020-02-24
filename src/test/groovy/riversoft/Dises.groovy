package riversoft

import riversoft.base.FieldParams
import riversoft.base.Game
import spock.lang.Specification

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class Dises extends Specification {
    def 'Тесты костей'() {
        given:
        List<Integer> dises
        List<Double> aveList = []
        int sessionCount = 10000
        int spinCount = 100000
        Random rand = new Random()
        int min = 6
        int max = 8
        double step = 0.1
        List<Double> range = []

        when:
        for (double i = min; i <= max; i += step) {
            range.add(i)
        }
        for (int i = 0; i < sessionCount; i++) {
            dises = []
            for (int j = 0; j < spinCount; j++) {
                dises.add(rand.nextInt(6) + rand.nextInt(6) + 2)
            }
            aveList.add((double) dises.sum() / spinCount)
        }

        Locale.getDefault()
        System.out.printf("Ave: %.2f\n\n", [(double) aveList.sum() / sessionCount])

        int count = aveList.count { t -> t <= range[0] }
        System.out.printf("%.1f - %.1f - %d - %.2f\n", [(double) 0, (double) min, count, (double) 100 * count / sessionCount])

        for (int i = 1; i < range.size(); i++) {
            count = aveList.count { t -> t > range[i - 1] && t <= range[i] }
            System.out.printf("%.1f - %.1f - %d - %.2f\n", [range[i - 1], range[i], count, (double) 100 * count / sessionCount])
        }

        double mi = 6.99
        double ma = 7.01
        count = aveList.count { t -> t >= mi && t <= ma }
        System.out.printf("\n%.2f - %.2f - %d - %.2f\n", [mi, ma, count, (double) 100 * count / sessionCount])

        mi = 6.98
        ma = 7.02
        count = aveList.count { t -> t >= mi && t <= ma }
        System.out.printf("%.2f - %.2f - %d - %.2f\n", [mi, ma, count, (double) 100 * count / sessionCount])

        mi = 6.97
        ma = 7.03
        count = aveList.count { t -> t >= mi && t <= ma }
        System.out.printf("%.2f - %.2f - %d - %.2f\n", [mi, ma, count, (double) 100 * count / sessionCount])

        mi = 6.96
        ma = 7.04
        count = aveList.count { t -> t >= mi && t <= ma }
        System.out.printf("%.2f - %.2f - %d - %.2f\n", [mi, ma, count, (double) 100 * count / sessionCount])

        mi = 6.95
        ma = 7.05
        count = aveList.count { t -> t >= mi && t <= ma }
        System.out.printf("%.2f - %.2f - %d - %.2f\n", [mi, ma, count, (double) 100 * count / sessionCount])

        then:
        0 == 0
    }
}
