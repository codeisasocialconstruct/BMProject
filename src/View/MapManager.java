package View;

public class MapManager
{
    //Legend:
    //N = null;
    //B = brick3
    //T = brick2
    //Y = brick1
    //W = wall
    private final static int GAME_WIDTH = 250;  //Map divided into blocks 50x50 pixels each
    private final static int GAME_HEIGHT = 250; //Map has size 16x12 blocks
    private final static int BLOCK_SIZE = 50;
    private static String[][] positionMatrix;
    private static String stream = "NNNNNNNBNNNBWBNNNBNNNNNNN";

    public MapManager()
    {
        positionMatrix = new String[GAME_WIDTH/BLOCK_SIZE][GAME_HEIGHT/BLOCK_SIZE];
    }

    public String[][] createPositionMatrix()
    {
        int counter = 0;
        for(int i=0; i<GAME_WIDTH/BLOCK_SIZE; i++ )
            for (int j=0; j<GAME_HEIGHT/BLOCK_SIZE; j++ )
            {
                char[] tmp = stream.toCharArray();
                switch (tmp[counter])
                {
                    case 'B':
                    {
                        positionMatrix[i][j] = "Brick3";
                        break;
                    }
                    case 'T':
                    {
                        positionMatrix[i][j] = "Brick2";
                        break;
                    }
                    case 'Y':
                    {
                        positionMatrix[i][j] = "Brick1";
                        break;
                    }
                    case 'W':
                    {
                        positionMatrix[i][j] = "Wall";
                        break;
                    }

                }
                counter++;
            }
        return positionMatrix;
    }
}
