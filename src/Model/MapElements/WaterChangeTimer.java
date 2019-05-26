package Model.MapElements;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WaterChangeTimer
{
    private Timer timer;
    private ArrayList<ImageView> waterList;
    private int flag = 1;
    private static final String WATER1 = "Model/MapElements/MapPieces/Water1.png";
    private static final String WATER2 = "Model/MapElements/MapPieces/Water2.png";

    public WaterChangeTimer(ArrayList<ImageView> waterList)
    {
        timer = new Timer();
        this.waterList = waterList;
    }

    public void moveWater()
    {
        timer.schedule(new waterChanger(),500);
    }

    public void stopMove()
    {
        timer.cancel();
    }


    private class waterChanger extends TimerTask
    {
        public void run()
        {
            if(flag == 1)
            {
                for(ImageView i : waterList)
                    i.setImage(new Image(WATER2));
                flag = 2;
            }
            else if(flag == 2)
            {
                for(ImageView i : waterList)
                    i.setImage(new Image(WATER1));
                flag = 1;
            }
            try
            {
                Thread.sleep(500);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
