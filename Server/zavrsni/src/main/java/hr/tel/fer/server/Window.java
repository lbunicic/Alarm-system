package hr.tel.fer.server;

import javax.persistence.Column;
/**
 * Created by lbunicic on 23/03/2017.
 */
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


//Entiy indicira da je to entitet u bazi podataka, nema @Table sto znacic da ce taj entitet biti u
//tablici Window
@Entity
public class Window {


    //zadajemo da je id ID objekta i pomoću strategija odredujemo da mora biti automatski generirano
    //Ostale varijable nisu naznacene i pretpostavljamo da ce svaka dobiti svoj stupac
    @Id
    private String id;
    
    private String roomName;
    private boolean state;
    private String name;

    //Spring ce koristiti taj konstruktor
    protected Window(){}

    public Window(String id,String name, String roomName, boolean state) {
        super();
        this.id = id;
        this.name = name;
        this.roomName = roomName;
        this.state = state;
    }
    
    public Window(String id){
    	this.id = id;
    }
    
    public Window(String id, boolean state){
    	this.id = id;
    	this.state = state;
    }
    
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
    public String toString() {
        return String.format(
                "Window[ roomName='%s', state='%s', name ='%s']",
                 roomName, state, name);
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


   

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }



}
