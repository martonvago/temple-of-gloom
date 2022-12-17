package student;

import game.GameState;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.LongStream;

public class ExplorerTest {
    @Test
    void test_game_performance() {
        final PrintStream stdout = System.out;
        final PrintStream stderr = System.err;

        final ByteArrayOutputStream inOut = new ByteArrayOutputStream();
        final PrintStream ioStream = new PrintStream(inOut);
        System.setOut(ioStream);
        System.setErr(ioStream);

        var numRuns = 1500;
        AtomicInteger failEscape = new AtomicInteger();

        var multipliers = new ArrayList<Double>(numRuns);
        var golds = new ArrayList<Integer>(numRuns);
        var scores = new ArrayList<Integer>(numRuns);
        var rng = new Random();
        rng.setSeed(25761623242424242L);

        LongStream.generate(rng::nextLong)
            .limit(numRuns)
            .forEach(l ->
                    GameState.runNewGame(l, false)
            );

        // This is a regex that matches and captures:
        // Bonus: 1.(\d)
        // Gold: (\d)
        // Score: (\d
        // (it will also capture Goore/Boore/Bold, but these do not appear in the output)
        // We later use this regex to capture the scores etc from the output.
        var re = Pattern.compile(
                "^(Sc|Go|Bo)(?:nus[^:]+: 1\\.?|(?:ld|ore)[^:]+: )([0-9]+)?$"
        );
        var logs = inOut.toString();

        logs.lines().forEach(s -> {
            if (s.startsWith("Your solution to explore")) {
                scores.add(0);
                return;
            } else if (s.startsWith("Your solution to escape")) {
                failEscape.set(failEscape.get() + 1);
            }

            var m = re.matcher(s);
            if (m.matches()) {
                switch (m.group(1)) {
                    case "Bo" -> multipliers.add(Double.valueOf(m.group(2) == null ? "0.0" : "1." + m.group(2)));
                    case "Go" -> golds.add(Integer.valueOf(m.group(2)));
                    case "Sc" -> scores.add(Integer.valueOf(m.group(2)));
                }
            }
        });

        System.setOut(stdout);
        System.setErr(stderr);

        System.out.println("Total runs        : " + numRuns);
        System.out.println("Failed Explore    : " + (numRuns - multipliers.size()));
        System.out.println("Failed Escape     : " + failEscape.get());
        System.out.println();

        System.out.println("Average Multiplier: " + getAverageMultiplier(multipliers, numRuns));
        System.out.println("Average Gold      : " + (golds.stream().reduce(Integer::sum).orElse(0) / numRuns));
        System.out.println("Average Score     : " + (scores.stream().reduce(Integer::sum).orElse(0) / numRuns));
    }

    private String getAverageMultiplier(List<Double> multipliers, int numRuns) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(multipliers.stream().reduce(Double::sum).orElse(0.0) / numRuns);
    }
}
