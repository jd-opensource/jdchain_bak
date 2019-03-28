package test.my.utils.serialize;

public class Car implements ICar{
	private int weight;
	
	private int cost;
	
	private Wheel wheel;

	/* (non-Javadoc)
	 * @see test.my.utils.serialize.ICar#getWeight()
	 */
	@Override
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	/* (non-Javadoc)
	 * @see test.my.utils.serialize.ICar#getCost()
	 */
	@Override
	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public Wheel getWheel() {
		return wheel;
	}

	public void setWheel(Wheel wheel) {
		this.wheel = wheel;
	}

	public boolean test(Car car) {
		return this.weight == car.weight;
	}

}