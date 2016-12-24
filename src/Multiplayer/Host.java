/* 
 * Copyright (C) 2016 Laurens Weyn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Multiplayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Laurens Weyn
 */
public class Host implements Runnable
{
    private ConcurrentHashMap<Integer, Integer> clientPositions = new ConcurrentHashMap<>();
    public boolean running = true;
    @Override
    public void run()
    {

        //This thread will wait for and take in clients
        try(ServerSocket listener = new ServerSocket(11037))
        {
            int clientNumber = 1;
            while (running)
            {
                new ClientManager(this, listener.accept(), clientNumber++).start();
            }
        }
        catch(IOException e)
        {
            //TODO error message
        }
        running = false;
    }
    void updateClient(int clientID, int position)
    {
        clientPositions.put(clientID, position);
    }
    void removeClient(int clientID)
    {
        clientPositions.remove(clientID);
    }
    public String getStatusText()
    {
        int ahead = 0;
        int behind = 0;
        int here = 0;
        int lost = 0;
        for(Integer i:clientPositions.values())
        {
            if(i == Integer.MIN_VALUE)lost++;
            else if(i > 0)ahead++;
            else if(i < 0)behind++;
            else here++;
        }
        String out = "";
        if(ahead > 0)
        {
            out += lost + " unknown";
        }
        if(ahead > 0)
        {
            if(!out.equals(""))out += ", ";
            out += ahead + " ahead";
        }
        if(behind > 0)
        {
            if(!out.equals(""))out += ", ";
            out += behind + " behind";
        }
        if(here > 0)
        {
            if(!out.equals(""))out += ", ";
            out += here + " in sync";
        }
        if(out.equals(""))
        {
            out = "Waiting for clients..";
        }
        return out + ".";
    }
}
