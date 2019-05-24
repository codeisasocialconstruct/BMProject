package View;

import java.sql.*;

public class DataBaseConnector
{
    private String question;
    private Connection connection;
    private ResultSet resultSet;
    private String name;
    private String map_stream;
    private int game_width;
    private int game_height;

    //============================

    public DataBaseConnector(String question)
    {
        this.question = question;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/battle_metropolis","root","");

            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(question);

        } catch (Exception e)
        {
            System.out.println("No database connection");
        }
    }

    public void getData()
    {
        try
        {
            while (resultSet.next())
            {
                System.out.print(resultSet.getInt(1) + " ");
                name = resultSet.getString(2);
                System.out.print(name + " ");
                game_width = resultSet.getInt(3);
                System.out.print(game_width + " ");
                game_height = resultSet.getInt(4);
                System.out.print(game_height + " ");
                map_stream = resultSet.getString(5);
                System.out.println(map_stream + " ");
            }
            connection.close();
        }
        catch(Exception e)
        {

        }
    }

    public String getName()
    {
        return name;
    }

    public String getMap_stream()
    {
        return map_stream;
    }

    public int getGame_width()
    {
        return game_width;
    }

    public int getGame_height()
    {
        return game_height;
    }
}
