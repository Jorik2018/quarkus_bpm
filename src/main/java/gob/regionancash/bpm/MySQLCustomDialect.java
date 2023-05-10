package gob.regionancash.bpm;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;


public class MySQLCustomDialect extends MySQL5Dialect {
	
	class ListAggFunction implements SQLFunction {

	    /**
	     * The pattern that describes how the function is build in SQL.
	     *
	     * Replacements:
	     * {path} - is replaced with the path of the list attribute
	     * {separator} - is replaced with the separator (defaults to '')
	     * {orderByPath} - is replaced by the path that is used for ordering the elements of the list
	     */
	    private String pattern;

	    /**
	     * Creates a new ListAggFunction definition which uses the ANSI SQL:2016 syntax.
	     */
	    public ListAggFunction() {
	        this("GROUP_CONCAT({path} ORDER BY {orderByPath} SEPARATOR {separator})");
	        //this("GROUP_CONCAT(DISTINCT {path} ORDER BY {orderByPath} SEPARATOR {separator})");
	    }

	    /**
	     * Creates a new ListAggFunction definition which uses a database specific syntax.
	     *
	     * @param pattern  The pattern that describes how the function is build in SQL.
	     */
	    public ListAggFunction(String pattern) {
	        this.pattern = pattern;
	    }


	    public boolean hasArguments() {
	        return true;
	    }

	    public boolean hasParenthesesIfNoArguments() {
	        return true;
	    }



		@Override
		public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
			return StringType.INSTANCE;
		}

		@Override
		public String render(Type firstArgumentType, List arguments, SessionFactoryImplementor factory)
				throws QueryException {
	        if (arguments.isEmpty() || arguments.size() > 3) {
	            throw new IllegalArgumentException(
	                    "Expected arguments for 'listagg': path [, separator [, order by path]]");
	        }
	        String path = (String) arguments.get(0);
	        String separator = arguments.size() < 2 ? "''" : (String) arguments.get(1);
	        String orderByPath = arguments.size() <= 2 ? path : (String) arguments.get(2);
	        return org.apache.commons.lang3.StringUtils.replaceEach(this.pattern, 
	        		new String[] { "{path}", "{separator}", "{orderByPath}" },
	                new String[] { path, separator, orderByPath })
	        		;
		}

	}
	 
    public MySQLCustomDialect() {
        super();
        registerFunction("group_concat", 
        		//new ListAggFunction()
        		new StandardSQLFunction("group_concat",null)
            );
        this.registerKeyword("SEPARATOR");
    }
    
}