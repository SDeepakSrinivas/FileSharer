
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerSide {

	public static void main(String[] args) throws IOException, ClassNotFoundException
	{

		p("Welcome to FileShare , A software to help you download files through TCP sockets !!");
		String osname=System.getProperty("os.name");
		p("It has been detected that you are using FileSharer on "+osname+"\n");
		boolean ubuntu=!osname.contains("Windows");
		//To get the directory the user of the server wants to share
		if(ubuntu==false)
		p("Enter the path of the directory you want to share..(With each '\\' replaced by '\\\\')");
		else
		p("Enter the path of the directory you want to share..( eg. /home/sds/Desktop )");

		Scanner inp=new Scanner(System.in);
		String Path=inp.nextLine();
		p(Path+" has been shared with all users of this FileSharer..\n");


		//To get the choice of the user, if he wants to share
		p("Do you want to start sharing? (y/n)  n::Quit");
		String ch=inp.nextLine();
		if(ch.equals("y"))
		p("A server of FileSharer is running at your end..\n");
		else if(ch.equals("n"))
		{
			p("FileSharer says BYE !! Come back later ...\n");
			return;
		}


		//Starting to share on port 7227 and wait for connections and prompt on accepting connection
		ServerSocket ss = new ServerSocket(7227);
		Socket socket = ss.accept();
		p("A connection has been accepted from "+socket.getInetAddress());
		p("");

		//Setting up the input and output streams to input and output data between server and client




		ObjectOutputStream op=new ObjectOutputStream(socket.getOutputStream());
		 op.flush();
		 ObjectInputStream ip=new ObjectInputStream(socket.getInputStream());




        //To output to the client all the files in the shared directory

        ArrayList<String> filesInDirec = new ArrayList<String>();
		File[] fileArr = new File(Path).listFiles();


		for (int i=0;i<fileArr.length;i++)
			filesInDirec.add(fileArr[i].getName());



		for(int i=0;i<filesInDirec.size();i++)
			if(fileArr[i].isDirectory())
			{
				op.writeObject("Folder :: "+filesInDirec.get(i));
		        op.flush();
		    }
			else
			{
				op.writeObject("File :: "+ filesInDirec.get(i));
				op.flush();
			}

		//To tell the client that we have stopped sending
		op.writeObject("-");
		op.flush();


		//To input commands
		boolean flag=true;
		while(flag)
		{
		String str=(String) ip.readObject();
		//To stop reading when the server stops sending strings
			int beg;
			if(str.contains("exit"))
				flag=false;
			else if(( beg=str.indexOf("download"))>=0)
			{
				String fName=str.substring(beg+9,str.length());

				if(filesInDirec.contains(fName))
				{
					String newPath;
					if(ubuntu)
					newPath=Path+"/"+fName;
					else
					newPath=Path+"\\\\"+fName;

						File fileToSend=new File(newPath);	//Setting the path name of the file to be downloaded
						sendFileToClient(fileToSend,socket);
				}
				flag=false;


			}
			else if((beg=str.indexOf("open"))>=0)
			{
				String fName=str.substring(beg+5,str.length());
				if(ubuntu)
				Path=Path+"/"+fName;
				else
					Path=Path+"\\\\"+fName;
			 filesInDirec = new ArrayList<String>();
				 fileArr = new File(Path).listFiles();


				for (int i=0;i<fileArr.length;i++) {
				    if (fileArr[i].exists()) {
				        filesInDirec.add(fileArr[i].getName());
				    }
				}

				op.writeObject("The files in "+fName+" are ::");
				op.flush();
				for(int i=0;i<filesInDirec.size();i++)
					if(filesInDirec.get(i).toString().contains("."))
					{
						op.writeObject("File :: "+filesInDirec.get(i));
				        op.flush();
				    }
					else
					{
						op.writeObject("Folder :: "+ filesInDirec.get(i));
						op.flush();
					}

				//To tell the client that we have stopped sending
				op.writeObject("-");
				op.flush();

			}
			else if((beg=str.indexOf("back"))>=0)
			{
				if(ubuntu)
						Path=Path.substring(0,Path.lastIndexOf("/"));
				else
				Path=Path.substring(0,Path.lastIndexOf("\\")-1);

			 filesInDirec = new ArrayList<String>();
				 fileArr = new File(Path).listFiles();


				for (int i=0;i<fileArr.length;i++) {
				    if (fileArr[i].exists()) {
				        filesInDirec.add(fileArr[i].getName());
				    }
				}

				op.writeObject("The files shared are ::");
				op.flush();
				for(int i=0;i<filesInDirec.size();i++)
					if(filesInDirec.get(i).toString().contains("."))
					{
						op.writeObject("File :: "+filesInDirec.get(i));
				        op.flush();
				    }
					else
					{
						op.writeObject("Folder :: "+ filesInDirec.get(i));
						op.flush();
					}

				//To tell the client that we have stopped sending
				op.writeObject("-");
				op.flush();

			}



		}

		p("\nThanks for using FileSharer !! Do promote File Sharing for SHARING IS CARING !! ");
	}



	private static void sendFileToClient(File fileToSend, Socket socket) throws IOException {

		byte[] fileArray=new byte[(int) fileToSend.length()];

		FileInputStream fis = new FileInputStream(fileToSend);
		BufferedInputStream bis = new BufferedInputStream(fis);
		bis.read(fileArray,0,fileArray.length);

		OutputStream os = socket.getOutputStream();
		p("Sending "+fileToSend.getAbsolutePath()+" to Client !!");
		os.write(fileArray,0,fileArray.length);
		os.flush();

		p(fileToSend.getName()+" has successfully been sent to the Client !!");

		socket.close();

	}


	private static void p(String s) {

		System.out.println(s);

	}

}
