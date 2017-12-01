package zavrsni.tel.fer.windowalarm;

/**
 * Created by lbunicic on 23/03/2017.
 */

public class Windows {

    //private Integer id;
    private String roomName;
    private boolean state;
    private String name;
    private String href;



    public Windows(String href, String name, String roomName, boolean state) {
        super();
        this.href=href;
        this.name = name;
        this.roomName = roomName;
        this.state = state;

    }

    @Override
    public String toString() {
        return String.format(
                "Window[ href='%s' roomName='%s', state='%s', name ='%s']",
                 href,roomName, state, name);
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
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
