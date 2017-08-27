package com.codenameflip.particleplayground;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.ParticleEffect;
import org.bukkit.Location;

import java.util.concurrent.ThreadLocalRandom;

public class SoulEffect extends Effect {

    public SoulEffect(EffectManager effectManager, Location location)
    {
        super(effectManager);

        this.setLocation(location);
        this.type = EffectType.DELAYED;
        this.period = 1;
        this.iterations = 300;
    }

    int step = 0;

    @Override
    public void onRun()
    {
        for (int x = 0; x < iterations; x++)
        {
            if (step > iterations)
                break;

            boolean changeX = ThreadLocalRandom.current().nextBoolean();
            boolean changeY = ThreadLocalRandom.current().nextBoolean();
            boolean changeZ = ThreadLocalRandom.current().nextBoolean();

            double deltaX = 0;
            double deltaY = 0;
            double deltaZ = 0;

            if (changeX)
                deltaX = ThreadLocalRandom.current().nextDouble(-0.1, 0.1);
            if (changeY)
                deltaY = ThreadLocalRandom.current().nextDouble(-0.1, 0.1);
            if (changeZ)
                deltaZ = ThreadLocalRandom.current().nextDouble(-0.1, 0.1);

            Location newLoc = getLocation().add(deltaX, deltaY, deltaZ);

            ParticleEffect.FLAME.display(0, 0, 0, 0, 1, newLoc, 50);
            setLocation(newLoc);
        }
    }
}
