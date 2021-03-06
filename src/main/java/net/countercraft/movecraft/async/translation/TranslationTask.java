/*
 * This file is part of Movecraft.
 *
 *     Movecraft is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Movecraft is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Movecraft.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.countercraft.movecraft.async.translation;

import net.countercraft.movecraft.Movecraft;
import net.countercraft.movecraft.async.AsyncTask;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.localisation.I18nSupport;
import net.countercraft.movecraft.utils.BoundingBoxUtils;
import net.countercraft.movecraft.utils.EntityUpdateCommand;
import net.countercraft.movecraft.utils.MapUpdateCommand;
import net.countercraft.movecraft.utils.MathUtils;
import net.countercraft.movecraft.utils.MovecraftLocation;

import org.apache.commons.collections.ListUtils;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;

public class TranslationTask extends AsyncTask {
	private TranslationTaskData data;

	public TranslationTask( Craft c, TranslationTaskData data ) {
		super( c );
		this.data = data;
	}

	@Override
	public void excecute() {
		MovecraftLocation[] blocksList = data.getBlockList();

		// blockedByWater=false means an ocean-going vessel
		boolean waterCraft=!getCraft().getType().blockedByWater();
		boolean hoverCraft = getCraft().getType().getCanHover();

		boolean airCraft = getCraft().getType().blockedByWater(); 
		
		int hoverLimit = getCraft().getType().getHoverLimit();
<<<<<<< HEAD
		// Find the waterline from the surrounding terrain or from the static level in the craft type
		int waterLine=0;
        	int minY=65535;
        
		if (waterCraft || hoverCraft) {
			int [][][] hb=getCraft().getHitBox();

			// start by finding the minimum and maximum y coord
			int maxY=-65535;
			for (int [][] i1 : hb) {
				for (int [] i2 : i1) {
					if(i2!=null) {
						if(i2[0]<minY) {
							minY=i2[0];
						}
						if(i2[1]>maxY) {
							maxY=i2[1];
						}
					}
				}
			}
            		int maxX=getCraft().getMinX()+hb.length;
			int maxZ=getCraft().getMinZ()+hb[0].length;  // safe because if the first x array doesn't have a z array, then it wouldn't be the first x array
			int minX=getCraft().getMinX();
			int minZ=getCraft().getMinZ();
            
	            if (waterCraft){	
	                if(getCraft().getType().getStaticWaterLevel()!=0) {
	                    if(waterLine<=maxY+1) {
	                        waterLine=getCraft().getType().getStaticWaterLevel();
	                    }
	                } else {
	                    // figure out the water level by examining blocks next to the outer boundaries of the craft
	                    for(int posY=maxY+1; (posY>=minY-1)&&(waterLine==0); posY--) {
	                        int posX;
	                        int posZ;
	                        posZ=minZ-1;
	                        for(posX=minX-1; (posX <= maxX+1)&&(waterLine==0); posX++ ) {
	                            if(getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==9) {
	                                waterLine=posY;
	                            }
	                        }
	                        posZ=maxZ+1;
	                        for(posX=minX-1; (posX <= maxX+1)&&(waterLine==0); posX++ ) {
	                            if(getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==9) {
	                                waterLine=posY;
	                            }
	                        }
	                        posX=minX-1;
	                        for(posZ=minZ; (posZ <= maxZ)&&(waterLine==0); posZ++ ) {
	                            if(getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==9) {
	                                waterLine=posY;
	                            }
	                        }
	                        posX=maxX+1;
	                        for(posZ=minZ; (posZ <= maxZ)&&(waterLine==0); posZ++ ) {
	                            if(getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==9) {
	                                waterLine=posY;
	                            }
	                        }
	                    }				
	                }
	
	                // now add all the air blocks found within the craft's hitbox immediately above the waterline and below to the craft blocks so they will be translated
	                HashSet<MovecraftLocation> newHSBlockList=new HashSet<MovecraftLocation>(Arrays.asList(blocksList));
	                int posY=waterLine+1;
	                for(int posX=minX; posX<maxX; posX++) {
	                    for(int posZ=minZ; posZ<maxZ; posZ++) {
	                        if(hb[posX-minX]!=null) {
	                            if(hb[posX-minX][posZ-minZ]!=null) {
	                                if(getCraft().getW().getBlockAt(posX,posY,posZ).getTypeId()==0 && posY>hb[posX-minX][posZ-minZ][0] && posY<hb[posX-minX][posZ-minZ][1]) {
	                                    MovecraftLocation l=new MovecraftLocation(posX,posY,posZ);
	                                    newHSBlockList.add(l);
	                                }
	                            }
	                        }
	                    }
	                }
	                // dont check the hitbox for the underwater portion. Otherwise open-hulled ships would flood.
	                for(posY=waterLine; posY>=minY; posY--) {
	                    for(int posX=minX; posX<maxX; posX++) {
	                        for(int posZ=minZ; posZ<maxZ; posZ++) {
	                            if(getCraft().getW().getBlockAt(posX,posY,posZ).getTypeId()==0) {
	                                MovecraftLocation l=new MovecraftLocation(posX,posY,posZ);
	                                newHSBlockList.add(l);
	                            }
	                        }
	                    }
	                }
	
	                blocksList=newHSBlockList.toArray(new MovecraftLocation[newHSBlockList.size()]);
	            }
        	}
=======

		int [][][] hb=getCraft().getHitBox();

		// start by finding the crafts borders
		int minY=65535;
		int maxY=-65535;
		for (int [][] i1 : hb) {
			for (int [] i2 : i1) {
				if(i2!=null) {
					if(i2[0]<minY) {
						minY=i2[0];
					}
					if(i2[1]>maxY) {
						maxY=i2[1];
					}
				}
			}
		}
		int maxX=getCraft().getMinX()+hb.length;
		int maxZ=getCraft().getMinZ()+hb[0].length;  // safe because if the first x array doesn't have a z array, then it wouldn't be the first x array
		int minX=getCraft().getMinX();
		int minZ=getCraft().getMinZ();
		
		// Load any chunks that you are moving into that are not loaded 
		for (int posX=minX+data.getDx();posX<=maxX+data.getDx();posX++) {
			for (int posZ=minZ+data.getDz();posZ<=maxZ+data.getDz();posZ++) {
				Chunk chunk=getCraft().getW().getBlockAt(posX,minY,posZ).getChunk();
				if (!chunk.isLoaded()) {
					chunk.load();
				}
			}
		}
		
		// treat sinking crafts specially
		if(getCraft().getSinking()) {
			waterCraft=true;
			hoverCraft=false;
		}
					
		// Find the waterline from the surrounding terrain or from the static level in the craft type
		int waterLine=0;
		if (waterCraft) {			
			if(getCraft().getType().getStaticWaterLevel()!=0) {
				if(waterLine<=maxY+1) {
					waterLine=getCraft().getType().getStaticWaterLevel();
				}
			} else {
				// figure out the water level by examining blocks next to the outer boundaries of the craft
				for(int posY=maxY+1; (posY>=minY-1)&&(waterLine==0); posY--) {
					int numWater=0;
					int numAir=0;
					int posX;
					int posZ;
					posZ=minZ-1;
					for(posX=minX-1; (posX <= maxX+1)&&(waterLine==0); posX++ ) {
						int typeID=getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId();
						if(typeID==9) 
							numWater++;
						if(typeID==0) 
							numAir++;
					}
					posZ=maxZ+1;
					for(posX=minX-1; (posX <= maxX+1)&&(waterLine==0); posX++ ) {
						int typeID=getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId();
						if(typeID==9) 
							numWater++;
						if(typeID==0) 
							numAir++;
					}
					posX=minX-1;
					for(posZ=minZ; (posZ <= maxZ)&&(waterLine==0); posZ++ ) {
						int typeID=getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId();
						if(typeID==9) 
							numWater++;
						if(typeID==0) 
							numAir++;
					}
					posX=maxX+1;
					for(posZ=minZ; (posZ <= maxZ)&&(waterLine==0); posZ++ ) {
						int typeID=getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId();
						if(typeID==9) 
							numWater++;
						if(typeID==0) 
							numAir++;
					}
					if(numWater>numAir) {
						waterLine=posY;
					}
				}				
			}

			// now add all the air blocks found within the craft's hitbox immediately above the waterline and below to the craft blocks so they will be translated
			HashSet<MovecraftLocation> newHSBlockList=new HashSet<MovecraftLocation>(Arrays.asList(blocksList));
			int posY=waterLine+1;
			for(int posX=minX; posX<maxX; posX++) {
				for(int posZ=minZ; posZ<maxZ; posZ++) {
					if(hb[posX-minX]!=null) {
						if(hb[posX-minX][posZ-minZ]!=null) {
							if(getCraft().getW().getBlockAt(posX,posY,posZ).getTypeId()==0 && posY>hb[posX-minX][posZ-minZ][0] && posY<hb[posX-minX][posZ-minZ][1]) {
								MovecraftLocation l=new MovecraftLocation(posX,posY,posZ);
								newHSBlockList.add(l);
							}
						}
					}
				}
			}
			// dont check the hitbox for the underwater portion. Otherwise open-hulled ships would flood.
			for(posY=waterLine; posY>=minY; posY--) {
				for(int posX=minX; posX<maxX; posX++) {
					for(int posZ=minZ; posZ<maxZ; posZ++) {
						if(getCraft().getW().getBlockAt(posX,posY,posZ).getTypeId()==0) {
							MovecraftLocation l=new MovecraftLocation(posX,posY,posZ);
							newHSBlockList.add(l);
						}
					}
				}
			}
				
			blocksList=newHSBlockList.toArray(new MovecraftLocation[newHSBlockList.size()]);
		}
		
>>>>>>> 09468ad2e81ba09ed1e218c73acf246533826885
		// check for fuel, burn some from a furnace if needed. Blocks of coal are supported, in addition to coal and charcoal
		double fuelBurnRate=getCraft().getType().getFuelBurnRate();
		if(fuelBurnRate!=0.0 && getCraft().getSinking()==false) {
			if(getCraft().getBurningFuel()<fuelBurnRate) {
				Block fuelHolder=null;
				for (MovecraftLocation bTest : blocksList) {
					Block b=getCraft().getW().getBlockAt(bTest.getX(), bTest.getY(), bTest.getZ());
					if(b.getTypeId()==61) {
						InventoryHolder inventoryHolder = ( InventoryHolder ) b.getState();
						if(inventoryHolder.getInventory().contains(263) || inventoryHolder.getInventory().contains(173)) {
							fuelHolder=b;
						}					
					}
				}
				if(fuelHolder==null) {
					fail( String.format( I18nSupport.getInternationalisedString( "Translation - Failed Craft out of fuel" ) ) );					
				} else {
					InventoryHolder inventoryHolder = ( InventoryHolder ) fuelHolder.getState();
					if(inventoryHolder.getInventory().contains(263)) {
						ItemStack iStack=inventoryHolder.getInventory().getItem(inventoryHolder.getInventory().first(263));
						int amount=iStack.getAmount();
						if(amount==1) {
							inventoryHolder.getInventory().remove(263);
						} else {
							iStack.setAmount(amount-1);
						}
						getCraft().setBurningFuel(getCraft().getBurningFuel()+7.0);
					} else {
						ItemStack iStack=inventoryHolder.getInventory().getItem(inventoryHolder.getInventory().first(173));
						int amount=iStack.getAmount();
						if(amount==1) {
							inventoryHolder.getInventory().remove(173);
						} else {
							iStack.setAmount(amount-1);
						}
						getCraft().setBurningFuel(getCraft().getBurningFuel()+79.0);

					}
				}
			} else {
				getCraft().setBurningFuel(getCraft().getBurningFuel()-fuelBurnRate);
			}
		}
		
		List<MovecraftLocation> tempBlockList=new ArrayList<MovecraftLocation>();
		HashSet<MovecraftLocation> existingBlockSet = new HashSet<MovecraftLocation>( Arrays.asList( blocksList ) );
		HashSet<EntityUpdateCommand> entityUpdateSet = new HashSet<EntityUpdateCommand>();
		Set<MapUpdateCommand> updateSet = new HashSet<MapUpdateCommand>();

		data.setCollisionExplosion(false);
		Set<MapUpdateCommand> explosionSet = new HashSet<MapUpdateCommand>();
		
		List<Material> harvestBlocks = getCraft().getType().getHarvestBlocks();
		List<MovecraftLocation> harvestedBlocks=new ArrayList<MovecraftLocation>(); //future for possible drop items
		
		int hoverOver = data.getDy();
		boolean clearNewData = false;
		boolean hoverUseGravity = getCraft().getType().getUseGravity();
		boolean checkHover = (data.getDx()!=0 || data.getDz()!=0);// we want to check only horizontal moves
		boolean canHoverOverWater = getCraft().getType().getCanHoverOverWater();
		
        for ( int i = 0; i < blocksList.length; i++ ) {
			MovecraftLocation oldLoc = blocksList[i];
			MovecraftLocation newLoc = oldLoc.translate( data.getDx(), data.getDy(), data.getDz() );
//			newBlockList[i] = newLoc;

			if ( newLoc.getY() > data.getMaxHeight() && newLoc.getY() > oldLoc.getY() ) {
				fail( String.format( I18nSupport.getInternationalisedString( "Translation - Failed Craft hit height limit" ) ) );
				break;
			} else if ( newLoc.getY() < data.getMinHeight()  && newLoc.getY() < oldLoc.getY() && getCraft().getSinking()==false ) {
				fail( String.format( I18nSupport.getInternationalisedString( "Translation - Failed Craft hit minimum height limit" ) ) );
				break;
			}

            boolean blockObstructed=false;
            boolean harvestBlock=false;
            Material testMaterial;
            
            testMaterial = getCraft().getW().getBlockAt(oldLoc.getX(), oldLoc.getY(), oldLoc.getZ() ).getType();
            if (testMaterial.equals(Material.CHEST) || testMaterial.equals(Material.TRAPPED_CHEST)){
                if (!checkChests(testMaterial, newLoc, existingBlockSet)){
                    //prevent chests collision
                    fail( String.format( I18nSupport.getInternationalisedString( "Translation - Failed Craft is obstructed" )+" @ %d,%d,%d", oldLoc.getX(), oldLoc.getY(), oldLoc.getZ() ) );
                    break;
                }
            } 
            
            if(getCraft().getSinking()) {
            	int testID=getCraft().getW().getBlockAt( newLoc.getX(), newLoc.getY(), newLoc.getZ() ).getTypeId();
				final int[] fallThroughBlocks = new int[]{ 0, 8, 9, 10, 11, 31, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 68, 69, 70, 72, 75, 76, 77, 78, 83, 93, 94, 111, 141, 142, 143, 171};
			
            	blockObstructed = !(Arrays.binarySearch(fallThroughBlocks, testID)>=0) && !existingBlockSet.contains( newLoc ); 
            } else if(!waterCraft) {
				// New block is not air or a piston head and is not part of the existing ship
                testMaterial = getCraft().getW().getBlockAt( newLoc.getX(), newLoc.getY(), newLoc.getZ() ).getType();
				blockObstructed = (!testMaterial.equals(Material.AIR) && !testMaterial.equals(Material.PISTON_EXTENSION)) && !existingBlockSet.contains( newLoc );
			} else {
			 	// New block is not air or water or a piston head and is not part of the existing ship
	            testMaterial = getCraft().getW().getBlockAt( newLoc.getX(), newLoc.getY(), newLoc.getZ() ).getType();
			 	blockObstructed = (!testMaterial.equals(Material.AIR) && !testMaterial.equals(Material.STATIONARY_WATER) 
			                      && !testMaterial.equals(Material.WATER) && !testMaterial.equals(Material.PISTON_EXTENSION)) && !existingBlockSet.contains( newLoc );
			}
			if (blockObstructed && hoverCraft){
			 	// New block is not harvested block
			 	if (harvestBlocks.contains(testMaterial) && !existingBlockSet.contains( newLoc )){
			 		blockObstructed = false;
			 		harvestBlock = true;
			 		harvestedBlocks.add(newLoc); //future for possible drop items
			 	} else {
			 		blockObstructed = true;
			 	}
			 }
			
<<<<<<< HEAD
			 if ( blockObstructed ) {
				 if (hoverCraft && checkHover){
				 	//we check one up ever, if it is hovercraft and one down if it's using gravity
				 	if (hoverOver == 0 && newLoc.getY() + 1 <= data.getMaxHeight() && oldLoc.getY() == minY  ){
=======
			if ( blockObstructed ) {
				if (hoverCraft && checkHover){
					//we check one up ever, if it is hovercraft and one down if it's using gravity
				 	if (hoverOver == 0 && newLoc.getY() + 1 <= data.getMaxHeight()){
>>>>>>> 09468ad2e81ba09ed1e218c73acf246533826885
				 		//first was checked actual level, now check if we can go up
                        			hoverOver = 1;
				 		data.setDy(1); 
				 		clearNewData = true;
				 	} else if (hoverOver >= 1){ 
				 		//check other options to go up
				 		if ( hoverOver < hoverLimit + 1  && newLoc.getY() + 1 <= data.getMaxHeight() && oldLoc.getY() == minY ){
                            				data.setDy(hoverOver + 1);
                            				hoverOver += 1;
                            				clearNewData = true;
				 		}else{
					 		if (hoverUseGravity && newLoc.getY() - hoverOver -1 >= data.getMinHeight()){
					 			//we are on the maximum of top 
					 			//if we can't go up so we test bottom side
					 			data.setDy(-1);
					 			hoverOver = -1;
					 		}else{
					 			// no way - back to original dY, turn off hovercraft for this move
					 			// and get original data again for all explosions
					 			data.setDy(0);
					 			hoverOver = 0;
					 			hoverCraft = false;
					 			hoverUseGravity=false;
					 			}
					 		clearNewData = true; 
					 		}
				 	}else if (hoverOver <=-1) {
				 		//we cant go down for 1 block, check more to hoverLimit
				 		if (hoverOver > -hoverLimit - 1 && newLoc.getY() - 1 >= data.getMinHeight()){
				 			data.setDy(hoverOver - 1); 
				 			hoverOver -= 1;
				 			clearNewData = true;
				 		}else{
				 				// no way - back to original dY, turn off hovercraft for this move
				 				// and get original data again for all explosions
				 			data.setDy(0);
				 			hoverOver = 0;
				 			hoverUseGravity = false;
				 			clearNewData = true; 
				 			hoverCraft = false;
				 			}
				 	}else{
				 			// no way - reached MaxHeight during looking new way upstairss 
				 			if (hoverUseGravity && newLoc.getY() -1 >= data.getMinHeight()){
				 				//we are on the maximum of top 
				 				//if we can't go up so we test bottom side
				 				data.setDy(-1);
				 				hoverOver = -1;
				 			}else{
				 				// - back to original dY, turn off hovercraft for this move
				 				// and get original data again for all explosions
				 				data.setDy(0);
				 				hoverOver = 0;
				 				hoverUseGravity = false;
				 				hoverCraft = false;
				 				}
				 			clearNewData = true; 
				 	}   
				// End hovercraft stuff
				} else {
					// handle sinking ship collisions
					if(getCraft().getSinking()) {
						if(getCraft().getType().getExplodeOnCrash() != 0.0F) {
					 		int explosionKey =  (int) (0-(getCraft().getType().getExplodeOnCrash()*100));
					 		if (!getCraft().getW().getBlockAt(oldLoc.getX(),oldLoc.getY(), oldLoc.getZ()).getType().equals(Material.AIR)){
					 			explosionSet.add( new MapUpdateCommand( oldLoc, explosionKey, getCraft() ) );
					 			data.setCollisionExplosion(true);
					 		}
						} else {
							// use the explosion code to clean up the craft, but not with enough force to do anything
					 		int explosionKey =  0-1;
					 		if (!getCraft().getW().getBlockAt(oldLoc.getX(),oldLoc.getY(), oldLoc.getZ()).getType().equals(Material.AIR)){
					 			explosionSet.add( new MapUpdateCommand( oldLoc, explosionKey, getCraft() ) );
					 			data.setCollisionExplosion(true);
					 		}
						}
					} else 
 				 	// Explode if the craft is set to have a CollisionExplosion. Also keep moving for spectacular ramming collisions
				 	if( getCraft().getType().getCollisionExplosion() == 0.0F) {
				 		fail( String.format( I18nSupport.getInternationalisedString( "Translation - Failed Craft is obstructed" )+" @ %d,%d,%d", oldLoc.getX(), oldLoc.getY(), oldLoc.getZ() ) );
				 		break;
				 		} else {
				 		int explosionKey =  (int) (0-(getCraft().getType().getCollisionExplosion()*100));
				 		if (!getCraft().getW().getBlockAt(oldLoc.getX(),oldLoc.getY(), oldLoc.getZ()).getType().equals(Material.AIR)){
				 			explosionSet.add( new MapUpdateCommand( oldLoc, explosionKey, getCraft() ) );
				 			data.setCollisionExplosion(true);
				 		}
			 		}
			 	}
			} else {
				int oldID = getCraft().getW().getBlockTypeIdAt( oldLoc.getX(), oldLoc.getY(), oldLoc.getZ() );
				// remove water from sinking crafts
				if(getCraft().getSinking()) {
					if((oldID==8 || oldID==9) && oldLoc.getY()>waterLine)
						oldID=0;
				}
				updateSet.add( new MapUpdateCommand( oldLoc, newLoc, oldID, getCraft() ) );
				tempBlockList.add(newLoc);
				
				if ( i == blocksList.length - 1 ){
					if ((hoverCraft && hoverUseGravity) || (hoverUseGravity && newLoc.getY() > data.getMaxHeight() && hoverOver ==0) ){
						//hovecraft using gravity or something else using gravity and flying over its limit
						int iFreeSpace = 0;
						//canHoverOverWater adds 1 to dY for better check water under craft 
						// best way should be expand selected region to each first blocks under craft
						if (hoverOver==0){
							//we go directly forward so we check if we can go down
							for (int ii = -1 ;ii > - hoverLimit - 2- (canHoverOverWater?0:1) ; ii--){
								if (!isFreeSpace(data.getDx(),hoverOver+ii, data.getDz(), blocksList, existingBlockSet, waterCraft, hoverCraft, harvestBlocks,canHoverOverWater, checkHover)){
									break;
								}
								iFreeSpace ++;
							}
							if (data.failed()) {
								break;
							}
							if (iFreeSpace > hoverLimit-(canHoverOverWater?0:1)){
								data.setDy(-1);
								hoverOver = -1;
								clearNewData=true;
							}
					 	}else if (hoverOver == 1 && !airCraft){
					 		//prevent fly heigher than hoverLimit
					 		for (int ii = -1 ;ii > - hoverLimit - 2; ii--){
					 			if (!isFreeSpace(data.getDx(),hoverOver+ii, data.getDz(), blocksList, existingBlockSet, waterCraft, hoverCraft, harvestBlocks,canHoverOverWater, checkHover)){
					 				break;
					 			}
					 			iFreeSpace ++;
					 		}
					 	if (data.failed()){
					 		break;
					 	}
					 	if (iFreeSpace > hoverLimit){
					 		fail( String.format( I18nSupport.getInternationalisedString( "Translation - Failed Craft hit height limit" ) ) );
					 		break;
					 	}
					}else if(hoverOver > 1){
					 	//prevent jump thru block  
					 	for (int ii = 1 ;ii < hoverOver - 1; ii++){
					 		if (!isFreeSpace(0,ii, 0, blocksList, existingBlockSet, waterCraft, hoverCraft, harvestBlocks,canHoverOverWater, checkHover)){
					 			break;
					 		}
					 	iFreeSpace ++;
					 	}
					 	if (data.failed()) {
							 break;
						}
						if (iFreeSpace + 2 < hoverOver){
							data.setDy(-1);
							hoverOver = -1;
							clearNewData=true;
						}
					}else if(hoverOver < -1){
						//prevent jump thru block  
						for (int ii = -1 ;ii > hoverOver + 1; ii--){
							if (!isFreeSpace(0,ii, 0, blocksList, existingBlockSet, waterCraft, hoverCraft, harvestBlocks,canHoverOverWater, checkHover)){
								break;
							}
							iFreeSpace ++;
						}
						if (data.failed()) {
							break;
							}
						if (iFreeSpace + 2 < -hoverOver){
							data.setDy(0);
							hoverOver = 0;
							hoverCraft = false;
							clearNewData=true;
						}
					}
					if (!canHoverOverWater){
						if (hoverOver >=1){
							//others hoverOver values we have checked jet
							for (int ii = hoverOver-1 ;ii >hoverOver - hoverLimit - 2; ii--){
								if (!isFreeSpace(0,ii, 0, blocksList, existingBlockSet, waterCraft, hoverCraft, harvestBlocks,canHoverOverWater, checkHover)){
									break;
								}
						 	iFreeSpace ++;
						 	}
							if (data.failed()){
								break;
								}
							}
						}
					}
				}
			}
			if (clearNewData){
				i = -1;
				tempBlockList.clear();
				updateSet.clear(); 
				harvestedBlocks.clear();
				data.setCollisionExplosion(false);
				explosionSet.clear();
				clearNewData = false;
			}
		}
		
		if(data.collisionExplosion()) {
			// mark the craft to check for sinking, remove the exploding blocks from the blocklist, and submit the explosions for map update
			for(MapUpdateCommand m : explosionSet) {
				if( existingBlockSet.contains(m.getNewBlockLocation()) ) {
					existingBlockSet.remove(m.getNewBlockLocation());
				}
			}
			MovecraftLocation[] newBlockList = (MovecraftLocation[]) existingBlockSet.toArray(new MovecraftLocation[0]);
			data.setBlockList( newBlockList );
			data.setUpdates(explosionSet.toArray( new MapUpdateCommand[1] ) );
			if(getCraft().getType().getSinkPercent()!=0.0) {
				getCraft().setLastBlockCheck(0);
			}
			// set the craft to immediately try to move again
			getCraft().setLastCruisUpdate(-1);
			fail( String.format( I18nSupport.getInternationalisedString( "Translation - Failed Craft is obstructed" ) ) );
		}

		if ( !data.failed() ) {
			MovecraftLocation[] newBlockList = (MovecraftLocation[]) tempBlockList.toArray(new MovecraftLocation[0]);
			data.setBlockList( newBlockList );

			//prevents torpedo and rocket pilots :)
			if (getCraft().getType().getMoveEntities() && getCraft().getSinking()==false){
				// Move entities within the craft
				List<Entity> eList=null;
				int numTries=0;
			
				while((eList==null)&&(numTries<100)) {
					try {
						eList=getCraft().getW().getEntities();
					}
					catch(java.util.ConcurrentModificationException e)
					{
						numTries++;
					}
				}

				Iterator<Entity> i=eList.iterator();
				while (i.hasNext()) {
					Entity pTest=i.next();
					if ( MathUtils.playerIsWithinBoundingPolygon( getCraft().getHitBox(), getCraft().getMinX(), getCraft().getMinZ(), MathUtils.bukkit2MovecraftLoc( pTest.getLocation() ) ) ) {
						if(pTest.getType()!=org.bukkit.entity.EntityType.DROPPED_ITEM ) {
							Location tempLoc = pTest.getLocation();
							if(getCraft().getPilotLocked()==true && pTest==CraftManager.getInstance().getPlayerFromCraft(getCraft())) {
								tempLoc.setX(getCraft().getPilotLockedX());
								tempLoc.setY(getCraft().getPilotLockedY());
								tempLoc.setZ(getCraft().getPilotLockedZ());
							} 
							tempLoc=tempLoc.add( data.getDx(), data.getDy(), data.getDz() );
							Location newPLoc=new Location(getCraft().getW(), tempLoc.getX(), tempLoc.getY(), tempLoc.getZ());
							newPLoc.setPitch(pTest.getLocation().getPitch());
							newPLoc.setYaw(pTest.getLocation().getYaw());
							
							EntityUpdateCommand eUp=new EntityUpdateCommand(pTest.getLocation().clone(),newPLoc,pTest);
							entityUpdateSet.add(eUp);
							if(getCraft().getPilotLocked()==true && pTest==CraftManager.getInstance().getPlayerFromCraft(getCraft())) {
								getCraft().setPilotLockedX(tempLoc.getX());
								getCraft().setPilotLockedY(tempLoc.getY());
								getCraft().setPilotLockedZ(tempLoc.getZ());
							}
						} else {
						//	pTest.remove(); Removed to test better fragile item removal
						}
					}
				}
			} else {
				//add releaseTask without playermove to manager
				if(getCraft().getType().getCruiseOnPilot()==false && getCraft().getSinking()==false)  // not necessary to release cruiseonpilot crafts, because they will already be released
					CraftManager.getInstance().addReleaseTask(getCraft());
			}
						
			// remove water near sinking crafts
			if(getCraft().getSinking()) {
				int posX;
				int posY=maxY;
				int posZ;
				if(posY>waterLine)
					for(posX=minX-1; posX <= maxX+1; posX++ ) {
						for(posZ=minZ-1; posZ <= maxZ+1; posZ++ ) {
							if(getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==9 || getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==8) {
								MovecraftLocation loc=new MovecraftLocation( posX, posY, posZ );
								updateSet.add( new MapUpdateCommand( loc, 0, getCraft() ) );	
							}
						}
					}
				for(posY=maxY+1; (posY>=minY-1)&&(posY>waterLine); posY--) {
					posZ=minZ-1;
					for(posX=minX-1; posX <= maxX+1; posX++ ) {
						if(getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==9 || getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==8) {
							MovecraftLocation loc=new MovecraftLocation( posX, posY, posZ );
							updateSet.add( new MapUpdateCommand( loc, 0, getCraft() ) );	
						}
					}
					posZ=maxZ+1;
					for(posX=minX-1; posX <= maxX+1; posX++ ) {
						if(getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==9 || getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==8) {
							MovecraftLocation loc=new MovecraftLocation( posX, posY, posZ );
							updateSet.add( new MapUpdateCommand( loc, 0, getCraft() ) );	
						}
					}
					posX=minX-1;
					for(posZ=minZ-1; posZ <= maxZ+1; posZ++ ) {
						if(getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==9 || getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==8) {
							MovecraftLocation loc=new MovecraftLocation( posX, posY, posZ );
							updateSet.add( new MapUpdateCommand( loc, 0, getCraft() ) );	
						}
					}
					posX=maxX+1;
					for(posZ=minZ-1; posZ <= maxZ+1; posZ++ ) {
						if(getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==9 || getCraft().getW().getBlockAt(posX, posY, posZ).getTypeId()==8) {
							MovecraftLocation loc=new MovecraftLocation( posX, posY, posZ );
							updateSet.add( new MapUpdateCommand( loc, 0, getCraft() ) );	
						}
					}
				}				
			}
			
			//Set blocks that are no longer craft to air
			List<MovecraftLocation> airLocation = ListUtils.subtract( Arrays.asList( blocksList ), Arrays.asList( newBlockList ) );

			for ( MovecraftLocation l1 : airLocation ) {
				// for watercraft, fill blocks below the waterline with water
				if(!waterCraft) {
					if(getCraft().getSinking()) {
						updateSet.add( new MapUpdateCommand( l1, 0, null, getCraft().getType().getSmokeOnSink()) );
					} else {
						updateSet.add( new MapUpdateCommand( l1, 0, null) );						
					}
				} else {
					if(l1.getY()<=waterLine) {
						// if there is air below the ship at the current position, don't fill in with water
						MovecraftLocation testAir=new MovecraftLocation(l1.getX(), l1.getY()-1, l1.getZ());
						while(existingBlockSet.contains(testAir)) {
							testAir.setY(testAir.getY()-1);
						}
						if(getCraft().getW().getBlockAt(testAir.getX(), testAir.getY(), testAir.getZ()).getTypeId()==0) {
							if(getCraft().getSinking()) {
								updateSet.add( new MapUpdateCommand( l1, 0, null, getCraft().getType().getSmokeOnSink()) );
							} else {
								updateSet.add( new MapUpdateCommand( l1, 0, null) );						
							}
						} else {
							updateSet.add( new MapUpdateCommand( l1, 9, null ) );
						}
					} else {
						if(getCraft().getSinking()) {
							updateSet.add( new MapUpdateCommand( l1, 0, null, getCraft().getType().getSmokeOnSink()) );
						} else {
							updateSet.add( new MapUpdateCommand( l1, 0, null) );						
						}
					}
				}
			}

			data.setUpdates(updateSet.toArray( new MapUpdateCommand[1] ) );
			data.setEntityUpdates(entityUpdateSet.toArray( new EntityUpdateCommand[1] ) );
			
			if ( data.getDy() != 0 ) {
				data.setHitbox( BoundingBoxUtils.translateBoundingBoxVertically( data.getHitbox(), data.getDy() ) );
			}

			data.setMinX( data.getMinX() + data.getDx() );
			data.setMinZ( data.getMinZ() + data.getDz() );
		}
	}

	private void fail( String message ) {
		data.setFailed( true );
		data.setFailMessage( message );
	}

	public TranslationTaskData getData() {
		return data;
	}
	
	private boolean isFreeSpace(int x, int y, int z, MovecraftLocation[] blocksList, HashSet<MovecraftLocation> existingBlockSet, boolean waterCraft, boolean hoverCraft, List<Material> harvestBlocks, boolean canHoverOverWater,boolean checkHover){
		boolean isFree = true;
		// this checking for hovercrafts should be faster with separating horizontal layers and checking only realy necesseries,
		// or more better: remember what checked in each translation, but it's beyond my current abilities, I will try to solve it in future
  
		for ( int i = 0; i < blocksList.length; i++ ) {
			MovecraftLocation oldLoc = blocksList[i];
			MovecraftLocation newLoc = oldLoc.translate( x, y, z);
		 
		 	Material testMaterial = getCraft().getW().getBlockAt( newLoc.getX(), newLoc.getY(), newLoc.getZ() ).getType();
		 	if (!canHoverOverWater){
		 		if ( testMaterial.equals(Material.STATIONARY_WATER) || testMaterial.equals(Material.WATER) ){                    
		 			fail (String.format(I18nSupport.getInternationalisedString( "Translation - Failed Craft over water" )));
		 		}
		 	}
		 
			if ( newLoc.getY() >= data.getMaxHeight() && newLoc.getY() > oldLoc.getY() && !checkHover ) {
			//if ( newLoc.getY() >= data.getMaxHeight() && newLoc.getY() > oldLoc.getY()) {
				isFree = false;
				break;
			} else if ( newLoc.getY() <= data.getMinHeight() && newLoc.getY() < oldLoc.getY() ) {
				isFree = false;
				break;
			}
		 	
		boolean blockObstructed=false;
		if(!waterCraft) {
			// New block is not air or a piston head and is not part of the existing ship
			blockObstructed = (!testMaterial.equals(Material.AIR) && !testMaterial.equals(Material.PISTON_EXTENSION)) && !existingBlockSet.contains( newLoc );
		} else {
			// New block is not air or water or a piston head and is not part of the existing ship
			blockObstructed = (!testMaterial.equals(Material.AIR) && !testMaterial.equals(Material.STATIONARY_WATER) 
								&& !testMaterial.equals(Material.WATER) && !testMaterial.equals(Material.PISTON_EXTENSION)) && !existingBlockSet.contains( newLoc );
		}
		if (blockObstructed && hoverCraft){
			// New block is not harvested block and is not part of the existing craft
			if (harvestBlocks.contains(testMaterial) && !existingBlockSet.contains( newLoc )){
				blockObstructed = false;
			}else{
				blockObstructed = true;
            }   
        }
	           
        if ( blockObstructed ) {
            isFree = false;
            break;
            } 
        }
        return isFree;	     
    }
    
    private boolean checkChests(Material mBlock, MovecraftLocation newLoc, HashSet<MovecraftLocation> existingBlockSet){
        Material testMaterial;
        MovecraftLocation aroundNewLoc;
        
        aroundNewLoc = newLoc.translate( 1, 0, 0);
        testMaterial = getCraft().getW().getBlockAt( aroundNewLoc.getX(), aroundNewLoc.getY(), aroundNewLoc.getZ()).getType();
        if (testMaterial.equals(mBlock)){
            if (!existingBlockSet.contains(aroundNewLoc)){
                return false;
            }
        }
        
        aroundNewLoc = newLoc.translate( -1, 0, 0);
        testMaterial = getCraft().getW().getBlockAt( aroundNewLoc.getX(), aroundNewLoc.getY(), aroundNewLoc.getZ()).getType();
        if (testMaterial.equals(mBlock)){
            if (!existingBlockSet.contains(aroundNewLoc)){
                return false;
            }
        }
        
        aroundNewLoc = newLoc.translate( 0, 0, 1);
        testMaterial = getCraft().getW().getBlockAt( aroundNewLoc.getX(), aroundNewLoc.getY(), aroundNewLoc.getZ()).getType();
        if (testMaterial.equals(mBlock)){
            if (!existingBlockSet.contains(aroundNewLoc)){
                return false;
            }
        }
        
        aroundNewLoc = newLoc.translate( 0, 0, -1);
        testMaterial = getCraft().getW().getBlockAt( aroundNewLoc.getX(), aroundNewLoc.getY(), aroundNewLoc.getZ()).getType();
        if (testMaterial.equals(mBlock)){
            if (!existingBlockSet.contains(aroundNewLoc)){
                return false;
            }
        }
        return true; 
    }
}
