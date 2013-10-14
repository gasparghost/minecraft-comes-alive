/*******************************************************************************
 * CommonProxy.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package mods.mca.core.forge;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import mods.mca.core.MCA;
import mods.mca.tileentity.TileEntityTombstone;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * The proxy used server-side.
 */
public class CommonProxy
{
	/**
	 * Registers all rendering information with Forge.
	 */
	public void registerRenderers() 
	{
		return;
	}

	/**
	 * Registers all tile entities.
	 */
	public void registerTileEntities()
	{
		GameRegistry.registerTileEntity(TileEntityTombstone.class, TileEntityTombstone.class.getSimpleName());
	}

	/**
	 * Registers tick handlers with Forge.
	 */
	public void registerTickHandlers()
	{
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
	}

	public void loadSkins()
	{
		try
		{
			File modFile = findModData();

			if (modFile.isFile())
			{
				loadSkinsFromFile(modFile);
			}

			else
			{
				loadSkinsFromFolder(modFile);
			}

			int loadedSkins = MCA.farmerSkinsMale.size() + MCA.librarianSkinsMale.size() +
					MCA.priestSkinsMale.size() + MCA.smithSkinsMale .size() +
					MCA.butcherSkinsMale.size() + MCA.guardSkinsMale.size() + 
					MCA.kidSkinsMale.size() + MCA.bakerSkinsMale.size() +
					MCA.minerSkinsMale.size() + MCA.farmerSkinsFemale.size() +
					MCA.librarianSkinsFemale.size() + MCA.priestSkinsFemale.size() +
					MCA.smithSkinsFemale.size() + MCA.butcherSkinsFemale.size() +
					MCA.guardSkinsFemale.size() + MCA.kidSkinsFemale.size() +
					MCA.bakerSkinsFemale.size() + MCA.minerSkinsFemale.size();

			MCA.instance.log("Loaded " + loadedSkins + " skins.");
		}

		catch (Throwable e)
		{
			MCA.instance.quitWithThrowable("Unexpected exception while loading skins.", e);
		}
	}

	private File findModData() throws ZipException, IOException
	{
		File modData = findModAsArchive();

		if (modData == null)
		{
			modData = findModAsFolder();
			
			if (modData == null)
			{
				MCA.instance.quitWithDescription("Unable to find file or folder containing MCA assets.");
			}
		}

		return modData;
	}

	private File findModAsArchive() throws ZipException, IOException
	{
		MCA.instance.log("Attempting to find MCA as an archive in the mods folder...");
		File returnFile = null;

		for (File f : new File(MCA.instance.runningDirectory + "/mods").listFiles())
		{
			if (f.isFile())
			{
				MCA.instance.log("Testing " + f.getName() + " for MCA data...");

				ZipFile archive = new java.util.zip.ZipFile(f);
				Enumeration enumerator = archive.entries();
				ZipEntry entry;					

				while (enumerator.hasMoreElements())
				{
					entry = (ZipEntry)enumerator.nextElement();

					//Test for random files unique to MCA.
					if (entry.getName().contains("mca/core/MCA.class") || entry.getName().contains("sleeping/EE1.png"))
					{
						returnFile = f;
						MCA.instance.log(f.getName() + " verified as MCA data file.");
						break;
					}
				}

				archive.close();
			}
		}

		return returnFile;
	}

	private File findModAsFolder()
	{
		MCA.instance.log("Attempting to find MCA as a folder in the mods folder...");
		File returnFile = null;
		
		for (File f : new File(MCA.instance.runningDirectory + "/mods").listFiles())
		{
			MCA.instance.log("Testing folder for MCA data: " + f.getName());
			
			if (f.isDirectory())
			{
				File testModFolder1 = new File(f.getAbsolutePath() + "/mca/core/MCA.class");
				File testModFolder2 = new File(f.getAbsolutePath() + "/mods/mca/textures/skins/EE1.png");
				
				if (testModFolder1.exists() || testModFolder2.exists())
				{
					MCA.instance.log("Folder verified as MCA data folder: " + f.getName());
					returnFile = f;
					break;
				}
			}
		}
		
		return returnFile;
	}

	private void loadSkinsFromFile(File modDataFile) throws ZipException, IOException
	{
		MCA.instance.log("Loading skins from MCA data file: " + modDataFile.getName() + "...");
		
		ZipFile modArchive = new ZipFile(modDataFile);
		Enumeration enumerator = modArchive.entries();

		while (enumerator.hasMoreElements())
		{
			//Loop through each entry within the JAR until the MCA folder is hit.
			ZipEntry file = (ZipEntry)enumerator.nextElement();
			String fileLocationInArchive = "/" + file.getName();

			if (fileLocationInArchive.contains("/mods/mca/textures/skins/") && fileLocationInArchive.contains("sleeping") == false)
			{
				if (fileLocationInArchive.contains("Farmer"))
				{
					//Remove everything but the Gender and ID to properly identify what the gender is.
					if (fileLocationInArchive.replace("textures/skins/Farmer", "").contains("M"))
					{
						MCA.farmerSkinsMale.add(fileLocationInArchive);
					}

					else
					{
						MCA.farmerSkinsFemale.add(fileLocationInArchive);
					}
				}

				else if (fileLocationInArchive.contains("Librarian"))
				{
					if (fileLocationInArchive.replace("textures/skins/Librarian", "").contains("M"))
					{
						MCA.librarianSkinsMale.add(fileLocationInArchive);
					}

					else
					{
						MCA.librarianSkinsFemale.add(fileLocationInArchive);
					}
				}

				else if (fileLocationInArchive.contains("Priest"))
				{
					if (fileLocationInArchive.replace("textures/skins/Priest", "").contains("M"))
					{
						MCA.priestSkinsMale.add(fileLocationInArchive);
					}

					else
					{
						MCA.priestSkinsFemale.add(fileLocationInArchive);
					}
				}

				else if (fileLocationInArchive.contains("Smith"))
				{
					if (fileLocationInArchive.replace("textures/skins/Smith", "").contains("M"))
					{
						MCA.smithSkinsMale.add(fileLocationInArchive);
					}

					else
					{
						MCA.smithSkinsFemale.add(fileLocationInArchive);
					}
				}

				else if (fileLocationInArchive.contains("Butcher"))
				{
					if (fileLocationInArchive.replace("textures/skins/Butcher", "").contains("M"))
					{
						MCA.butcherSkinsMale.add(fileLocationInArchive);
					}
				}

				else if (fileLocationInArchive.contains("Guard"))
				{
					if (fileLocationInArchive.replace("textures/skins/Guard", "").contains("M"))
					{
						MCA.guardSkinsMale.add(fileLocationInArchive);
					}

					else
					{
						MCA.guardSkinsFemale.add(fileLocationInArchive);
					}
				}

				else if (fileLocationInArchive.contains("Kid"))
				{
					if (fileLocationInArchive.replace("textures/skins/Kid", "").contains("M"))
					{
						MCA.kidSkinsMale.add(fileLocationInArchive);
					}

					else
					{
						MCA.kidSkinsFemale.add(fileLocationInArchive);
					}
				}

				else if (fileLocationInArchive.contains("Baker"))
				{
					if (fileLocationInArchive.replace("textures/skins/Baker", "").contains("M"))
					{
						MCA.bakerSkinsMale.add(fileLocationInArchive);
					}

					else
					{
						MCA.bakerSkinsFemale.add(fileLocationInArchive);
					}
				}

				else if (fileLocationInArchive.contains("Miner"))
				{
					if (fileLocationInArchive.replace("textures/skins/Miner", "").contains("M"))
					{
						MCA.minerSkinsMale.add(fileLocationInArchive);
					}

					else
					{
						MCA.minerSkinsFemale.add(fileLocationInArchive);
					}
				}
			}
		}

		modArchive.close();
	}

	private void loadSkinsFromFolder(File modFolder)
	{
		MCA.instance.log("Loading skins from folder containing MCA data: " + modFolder.getName() + "...");

		String skinsFolder = modFolder + "/mods/mca/textures/skins/";
		String sleepingSkinsFolder = modFolder + "/mods/mca/textures/skins/sleeping/";

		for (File fileName : new File(skinsFolder).listFiles())
		{
			//Fix the file's location in the folder and determine what type of villager the skin belongs to.
			//Skins are named like [Profession][Gender][ID].png.
			String fileLocation = skinsFolder.replace(modFolder.getName() + "/mods/mca/", "") + "/" + fileName.getName();

			if (fileLocation.contains("Farmer"))
			{
				//Remove everything but the Gender and ID to properly identify what the gender is.
				if (fileLocation.replace("textures/skins/Farmer", "").contains("M"))
				{
					MCA.farmerSkinsMale.add(fileLocation);
				}

				else
				{
					MCA.farmerSkinsFemale.add(fileLocation);
				}
			}

			else if (fileLocation.contains("Librarian"))
			{
				if (fileLocation.replace("textures/skins/Librarian", "").contains("M"))
				{
					MCA.librarianSkinsMale.add(fileLocation);
				}

				else
				{
					MCA.librarianSkinsFemale.add(fileLocation);
				}
			}

			else if (fileLocation.contains("Priest"))
			{
				if (fileLocation.replace("textures/skins/Priest", "").contains("M"))
				{
					MCA.priestSkinsMale.add(fileLocation);
				}

				else
				{
					MCA.priestSkinsFemale.add(fileLocation);
				}
			}

			else if (fileLocation.contains("Smith"))
			{
				if (fileLocation.replace("textures/skins/Smith", "").contains("M"))
				{
					MCA.smithSkinsMale.add(fileLocation);
				}

				else
				{
					MCA.smithSkinsFemale.add(fileLocation);
				}
			}

			else if (fileLocation.contains("Butcher"))
			{
				if (fileLocation.replace("textures/skins/Butcher", "").contains("M"))
				{
					MCA.butcherSkinsMale.add(fileLocation);
				}
			}

			else if (fileLocation.contains("Guard"))
			{
				if (fileLocation.replace("textures/skins/Guard", "").contains("M"))
				{
					MCA.guardSkinsMale.add(fileLocation);
				}

				else
				{
					MCA.guardSkinsFemale.add(fileLocation);
				}
			}

			else if (fileLocation.contains("Kid"))
			{
				if (fileLocation.replace("textures/skins/Kid", "").contains("M"))
				{
					MCA.kidSkinsMale.add(fileLocation);
				}

				else
				{
					MCA.kidSkinsFemale.add(fileLocation);
				}
			}

			else if (fileLocation.contains("Baker"))
			{
				if (fileLocation.replace("textures/skins/Baker", "").contains("M"))
				{
					MCA.bakerSkinsMale.add(fileLocation);
				}

				else
				{
					MCA.bakerSkinsFemale.add(fileLocation);
				}
			}

			else if (fileLocation.contains("Miner"))
			{
				if (fileLocation.replace("textures/skins/Miner", "").contains("M"))
				{
					MCA.minerSkinsMale.add(fileLocation);
				}

				else
				{
					MCA.minerSkinsFemale.add(fileLocation);
				}
			}
		}
	}
}