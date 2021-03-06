package org.osgeo.grass.r3;

import org.jgrasstools.grass.utils.ModuleSupporter;

import oms3.annotations.Author;
import oms3.annotations.Documentation;
import oms3.annotations.Label;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.UI;
import oms3.annotations.Keywords;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Out;
import oms3.annotations.Status;

@Description("Convert a 3D ASCII raster text file into a (binary) 3D raster map layer")
@Author(name = "Grass Developers Community", contact = "http://grass.osgeo.org")
@Keywords("raster3d, voxel, import")
@Label("Grass/Raster 3D Modules")
@Name("r3__in__ascii")
@Status(Status.CERTIFIED)
@License("General Public License Version >=2)")
public class r3__in__ascii {

	@Description("Ascii raster map to be imported")
	@In
	public String $$inputPARAMETER;

	@UI("outfile,grassfile")
	@Description("Name for output raster3d map")
	@In
	public String $$outputPARAMETER;

	@Description("String representing NULL value data cell (use 'none' if no such value) (optional)")
	@In
	public String $$nvPARAMETER = "none";

	@Description("Data type used in the output file (optional)")
	@In
	public String $$typePARAMETER = "default";

	@Description("Precision used in the output file (default, max, or 0 to 52) (optional)")
	@In
	public String $$precisionPARAMETER = "default";

	@Description("The compression method used in the output file (optional)")
	@In
	public String $$compressionPARAMETER = "default";

	@Description("The dimensions of the tiles used in the output file (optional)")
	@In
	public String $$tiledimensionPARAMETER = "default";

	@Description("Allow output files to overwrite existing files")
	@In
	public boolean $$overwriteFLAG = false;

	@Description("Verbose module output")
	@In
	public boolean $$verboseFLAG = false;

	@Description("Quiet module output")
	@In
	public boolean $$quietFLAG = false;


	@Execute
	public void process() throws Exception {
		ModuleSupporter.processModule(this);
	}

}
