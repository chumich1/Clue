package misc;

public class Suggestion {
	
	@Override
	public String toString() {
		return (person.getName() + " in the "+ room.getName()+ " with the "+ weapon.getName());
	}
	private Card person;
	private Card room;
	private Card weapon;
	
	public Card getPerson() {
		return person;
	}
	public void setPerson(Card person) {
		this.person = person;
	}
	public Card getRoom() {
		return room;
	}
	public void setRoom(Card room) {
		this.room = room;
	}
	public Card getWeapon() {
		return weapon;
	}
	public void setWeapon(Card weapon) {
		this.weapon = weapon;
	}
	
	
	
}
