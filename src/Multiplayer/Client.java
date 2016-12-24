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

import UI.UI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 *
 * @author Laurens Weyn
 */
public class Client implements Runnable
{
    private Socket socket;
    private int position = 0;
    private final static Charset encoding = Charset.forName("UTF-8");
    private String lastText;
    
    public boolean running = true;
    public Client(Socket socket)
    {
        this.socket = socket;
        lastText = UI.text;
    }

    @Override
    public void run()
    {
        try
        {
            PacketReader in = new PacketReader(new InputStreamReader(socket.getInputStream(), encoding));
            OutputStream out = socket.getOutputStream();
            while(running)
            {
                String bits[] = in.getPacket();
                if(bits != null)switch(bits[0])
                {
                    case "U"://send C (what's your text?)
                        out.write(("C\t" + UI.text + "\n").getBytes(encoding));
                        break;
                    case "C"://text is now [arg] for me, send R
                        {
                            int pos = UI.log.linePos(bits[1]);
                            out.write(("R\t" + pos + "\n").getBytes(encoding));
                            //client is behind (in our logs)
                            if(pos >= 0)
                            {
                                position = -pos;
                            }
                        }
                        break;
                    case "R"://that line is at pos [arg] for me
                        {
                            int pos = Integer.parseInt(bits[1]);
                            //client is ahead (in our logs)
                            if(pos >= 0)position = pos;
                            else//line unknown, request line (client behind)
                            {
                                out.write("U\n".getBytes(encoding));
                            }
                        }
                        break;
                }
                
                //check for updates on our text
                String text = UI.text;
                if(text.equals(lastText) == false)
                {
                    out.write(("C\t" + text + "\n").getBytes(encoding));
                    lastText = text;
                    position = Integer.MIN_VALUE;//unknown until response
                }
                //wait a bit before we run again
                try
                {
                    Thread.sleep(100);
                }catch(InterruptedException e){}
                
            }
            out.close();
            in.close();
        }catch(IOException e)
        {
            
        }
        running = false;
    }
    public String getStatusText()
    {
        if(position == 0)return "in sync";
        if(position > 0)return position + " lines ahead";
        else return position + " lines behind";
    }
}
