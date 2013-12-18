package all.entities;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Record implements Serializable {
	private int id;
	private int year;
	private int quarter;
	private int profitTax;
	private int userId;
	
	public Record(int year, int quarter, int profitTax) {
		setYear(year);
		setQuarter(quarter);
		setProfitTax(profitTax);
	}

	public Record(int year, int quarter, int profitTax, int userId) {
		setYear(year);
		setQuarter(quarter);
		setProfitTax(profitTax);
		setUserId(userId);
	}
	
	public Record(int id, int year, int quarter, int profitTax, int userId) {
		setId(id);
		setYear(year);
		setQuarter(quarter);
		setProfitTax(profitTax);
		setUserId(userId);
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}
	
	public void setProfitTax(int profitTax) {
		this.profitTax = profitTax;
	}
	
	public int getId() {
		return id;
	}
	
	public int getUserId() {
		return userId;
	}

	public int getYear() {
		return year;
	}

	public int getQuarter() {
		return quarter;
	}
	
	public int getProfitTax() {
		return profitTax;
	}
}
