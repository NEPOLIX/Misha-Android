package com.nepolix.misha.db.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Behrooz Shahriari
 * @since 7/22/17
 */

public
class MishaCache
{
	 
	 private final static int TOTAL_CACHE = 100000;
	 
	 private Map< String, Map< String, Object > > collectionCache;
	 
	 MishaCache ( )
	 {
			
			collectionCache = new LinkedHashMap<> ( );
	 }
	 
	 public
	 void add ( String collectionName,
							String key,
							Object value )
	 {
			
			Map< String, Object > iMap = collectionCache.get ( collectionName );
			if ( iMap == null )
			{
				 iMap = new ConcurrentHashMap<> ( );
				 collectionCache.put ( collectionName, iMap );
			}
			if ( key != null ) iMap.put ( key, value );
			limitCache ( );
	 }
	 
	 private
	 void limitCache ( )
	 {
			
			Iterator< String > iterator = collectionCache.keySet ( ).iterator ( );
			int                size     = 0;
			for ( Map map : collectionCache.values ( ) )
			{
				 size += map.size ( );
			}
			HashSet< String > rIds = new HashSet<> ( );
			while ( size > TOTAL_CACHE && iterator.hasNext ( ) )
			{
				 String k = iterator.next ( );
				 rIds.add ( k );
				 Map map = collectionCache.get ( k );
				 size -= map.size ( );
			}
			for ( String x : rIds )
			{
				 collectionCache.remove ( x );
			}
	 }
	 
	 public
	 < T > T get ( String collectionName,
								 String key )
	 {
			
			Object                o    = null;
			Map< String, Object > iMap = collectionCache.get ( collectionName );
			if ( iMap != null && key != null ) o = iMap.get ( key );
			return ( T ) o;
	 }
	 
	 public
	 List< Object > getAll ( String collectionName )
	 {
			
			List< Object >        list = new ArrayList<> ( );
			Map< String, Object > iMap = collectionCache.get ( collectionName );
			if ( iMap != null )
			{
				 list.addAll ( iMap.values ( ) );
			}
			return list;
	 }
	 
	 public
	 void remove ( String collectionName,
								 String key )
	 {
			
			Map< String, Object > iMap = collectionCache.get ( collectionName );
			if ( iMap != null && key != null )
			{
				 iMap.remove ( key );
			}
	 }
	 
	 public
	 void remove ( String collectionName )
	 {
			
			collectionCache.remove ( collectionName );
	 }
	 
	 public
	 void cleanState ( )
	 {
			
			collectionCache.clear ( );
	 }
}
