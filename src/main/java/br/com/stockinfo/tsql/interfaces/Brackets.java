package br.com.stockinfo.tsql.interfaces;

/**
 * Classe para auxiliar na colocação de brackets nas consultas
 */
public class Brackets{
	public boolean open;
	public Integer quantity = 1;

	public Brackets(boolean open) {
		this.open = open;
	}
}
