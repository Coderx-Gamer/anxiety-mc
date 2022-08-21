package net.anxietymc;

import com.google.common.collect.ImmutableList;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Random;

public class Constants {
    // all possible anxiety messages

    public static final List<Text> ANXIETY_MESSAGES = ImmutableList.copyOf(new Text[]{
            Text.of("Don't look down.."),
            Text.of("That fall is the end.."),
            Text.of("Are you in hardcore mode?"),
            Text.of("Once you fall.. there is no return...."),
            Text.of("Don't speed bridge.."),
            Text.of("Don't let go of the shift key..."),
            Text.of("If you fall.. you will quit."),
            Text.of("Friendly reminder that falling will lose all your progress...."),
            Text.of("You won't be so lucky this time.."),
            Text.of("Take a minute to question if you are going to fall."),
            Text.of("Will you fall?")
    });

    // get a random anxiety message from ANXIETY_MESSAGES
    public static Text randomAnxietyMessage() {
        Random random = new Random();
        return ANXIETY_MESSAGES.get(random.nextInt(ANXIETY_MESSAGES.size()));
    }
}
