package all.entities;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Product implements Serializable {
	private int id;
	private String name;
	private int vatRate;
	private int sellingPrice;
	private int costPrice;
	private int quantity;
	private int recordId;
	
	public Product(String name, int sellingPrice, int costPrice, int vatRate, int quantity) {
		setName(name);
		setSellingPrice(sellingPrice);
		setCostPrice(costPrice);
		setVatRate(vatRate);
		setQuantity(quantity);
	}
	
	public Product(int id, String name, int sellingPrice, int costPrice, int vatRate, int quantity) {
		setId(id);
		setName(name);
		setSellingPrice(sellingPrice);
		setCostPrice(costPrice);
		setVatRate(vatRate);
		setQuantity(quantity);
	}

	public Product(String name, int sellingPrice, int costPrice, int vatRate, int quantity, int recordId) {
		setName(name);
		setSellingPrice(sellingPrice);
		setCostPrice(costPrice);
		setVatRate(vatRate);
		setQuantity(quantity);
		setRecordId(recordId);
	}
	
	public Product(int id, int vatRate, int sellingPrice, int costPrice, int quantity, int recordId) {
		setId(id);
		setSellingPrice(sellingPrice);
		setCostPrice(costPrice);
		setVatRate(vatRate);
		setQuantity(quantity);
		setRecordId(recordId);
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setVatRate(int vatRate) {
		this.vatRate = vatRate;
	}

	public void setSellingPrice(int sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public void setCostPrice(int costPrice) {
		this.costPrice = costPrice;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public int getVatRate() {
		return vatRate;
	}

	public int getSellingPrice() {
		return sellingPrice;
	}

	public int getCostPrice() {
		return costPrice;
	}

	public int getQuantity() {
		return quantity;
	}
	
	public int getRecordId() {
		return recordId;
	}
}
