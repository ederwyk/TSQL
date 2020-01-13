package br.com.stockinfo.tsql.util;

public class ExecutionException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExecutionException(Throwable e) {
        super(e);
    }
}
