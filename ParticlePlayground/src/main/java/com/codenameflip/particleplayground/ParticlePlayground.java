package com.codenameflip.particleplayground;

import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public final class ParticlePlayground extends JavaPlugin {

    private static ParticlePlayground instance;
    private EffectManager effectManager;

    @Override
    public void onEnable()
    {
        // Plugin startup logic

        EffectLib lib = EffectLib.instance();
        effectManager = new EffectManager(lib);

        getCommand("playParticle").setExecutor(this);

        instance = this;
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }

    public static ParticlePlayground get()
    {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (label.equalsIgnoreCase("playParticle") && sender instanceof Player)
        {
            Location currentLocation = ((Player) sender).getEyeLocation();

            int strands = 1;

            for (int j = 0; j < strands; j++)
            {
                for (double i = 0; i < 200; i += .01)
                {
                    boolean changeX = ThreadLocalRandom.current().nextBoolean();
                    boolean changeY = ThreadLocalRandom.current().nextBoolean();
                    boolean changeZ = ThreadLocalRandom.current().nextBoolean();
                    boolean neg = ThreadLocalRandom.current().nextBoolean();

                    double deltaX = 0;
                    double deltaY = 0;
                    double deltaZ = 0;

                    if (changeX)
                        deltaX = getRandom(-0.01, 0.01);
                    if (changeY)
                        deltaY = getRandom(-0.01, 0.01);
                    if (changeZ)
                        deltaZ = getRandom(-0.01, 0.01);

                    Location newLoc;

                    if (!neg)
                        newLoc = currentLocation.add(deltaX, deltaY, deltaZ);
                    else
                        newLoc = currentLocation.subtract(deltaX, deltaY, deltaZ);

                    ParticleEffect.FLAME.display(0, 0, 0, 0, 2, newLoc, 50);
                    currentLocation = newLoc;
                }
            }
        }

        return false;
    }

    private double getRandom(double min, double max)
    {
        return ThreadLocalRandom.current().nextDouble((max - min) + 1) + min;
    }

}
