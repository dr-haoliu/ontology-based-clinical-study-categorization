package edu.columbia.dbmi.wenglab.mesh.utils;


import java.util.UUID;
public class GenerateUUID
{
    private static long previousTimeMillis = System.currentTimeMillis();
    private static long counter = 0L;

    public static void main(String[] args)
    {
        UUID uuid=UUID.randomUUID(); //Generates random UUID
        System.out.println(uuid);
        System.out.println("nextID() = " + nextID());
    }



    public static synchronized long nextID() {
        long currentTimeMillis = System.currentTimeMillis();
        counter = (currentTimeMillis == previousTimeMillis) ? (counter + 1L) & 1048575L : 0L;
        previousTimeMillis = currentTimeMillis;
        long timeComponent = (currentTimeMillis & 8796093022207L) << 20;
        return timeComponent | counter;
    }

    public static long getFixedLong(){
        return 1720342000000000000L;
    }

}

