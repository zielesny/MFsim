# MFsim
MFsim - An open Java all-in-one rich-client simulation environment for mesoscopic simulation

MFsim is an open Java all-in-one rich-client computing environment for mesoscopic simulation with [Jdpd](https://github.com/zielesny/Jdpd) as its default simulation kernel for Molecular Fragment Dissipative Particle Dynamics (DPD). The environment integrates and supports the complete preparation-simulation-evaluation triad of a mesoscopic simulation task. Productive highlights are a [SPICES](https://github.com/zielesny/SPICES) molecular structure editor, a PDB-to-SPICES parser for particle-based peptide/protein representations, a support of polymer definitions, a compartment editor for complex simulation box start configurations, interactive and flexible simulation box views including analytics, simulation movie generation or animated diagrams. As an open project, MFsim enables customized extensions for different fields of research.

MFsim uses several open libraries (see *MFSimVersionHistory.txt* for details and references below) and is published as open source under the GNU General Public License version 3 (see *LICENSE*).

MFsim has been described in the scientific literature and used for a biomolecular DPD study of cyclotide/membrane interactions (see references below).

### Content

- **MFSimVersionHistory.txt** shows the version history of MFsim.
- The **src** subfolder contains all (Netbeans) source code packages.
- The **test** subfolder contains Unit tests.
- The **lib** subfolder comprises the open Java libraries used by MFsim (see *MFSimVersionHistory.txt* for details).
- The **javadoc** subfolder provides the source code Javadoc HTML documentations.
- The **MFsim_Source** subfolder is a complete MFsim installation with all Java bytecode libraries including *MFsim.jar* and splash image *MFsimSplash.jpg* (in subfolder *lib*), MFsim version history (in subfolder *info*), particle set files (in subfolder *particles*), tutorial PDF documents (in subfolder *tutorials*) and utility programs (in subfolder *winUtils* for Windows OS only). *MFsim.jar* may be started with an appropriate batch file of the operating system.
- The **tutorials** subfolder offers MFsim tutorials (see corresponding *README*).
- The subfolders **2020 Cyclotide-membrane interaction study**, **2021 Cyclotide-membrane interaction study**, **2022 C10E4-water bilayer formation study** and **2022 Cyclotide-membrane electrostatics study**  contain supplementary information for specific studies performed with MFsim/Jdpd (see corresponding *README* files in each subfolder).

### Installer for Windows OS
* A convenient [Windows OS installer executable for MFsim](https://w-hs.sciebo.de/s/DVVqGn3rtAnhoUs) is available. Download the installer executable (via [link](https://w-hs.sciebo.de/s/DVVqGn3rtAnhoUs)), start and follow the instructions to install MFsim. Note, that the installation includes a full Java Runtime Environment (JRE). After installation, create a shortcut to an appropriate MFsim start batch file on your Windows desktop (e.g. for MFsim to use up to 16 gigabyte of RAM copy a shortcut to batch file *Start_MFsim_64bit_16GB.bat* which is located in the MFsim program folder *C:\Program Files\GNWI\MFsim 2.4.0.0*). To start MFsim double click the created shortcut. MFsim can be uninstalled by the provided *Uninstall.exe* executable in the MFsim program folder or standard Windows functions.

### References

- [K. van den Broek, M. Daniel, M. Epple, J.-M. Hein, H. Kuhn, S. Neumann, A. Truszkowski and A. Zielesny, _MFsim - an open Java all-in-one rich-client simulation environment for mesoscopic simulation_, Journal of Cheminformatics (2020), 12:29](https://doi.org/10.1186/s13321-020-00432-9)
- [K. van den Broek, M. Epple, L. S. Kersten, H. Kuhn and A. Zielesny, _Quantitative Estimation of Cyclotide-Induced Bilayer Membrane Disruption by Lipid Extraction with Mesoscopic Simulation_, Journal of Chemical Information an Modeling (2021), 61, 3027-3040](https://doi.org/10.1021/acs.jcim.1c00332) ([Link to ChemRxiv preprint](https://doi.org/10.26434/chemrxiv.14135783.v1))

MFsim is an integration project of the open molecular fragment cheminformatics roadmap

- [A. Truszkowski, M. Daniel, H. Kuhn, S. Neumann, C. Steinbeck, A. Zielesny and M. Epple, _A molecular fragment cheminformatics roadmap for mesoscopic simulation_, Journal of Cheminformatics (2014), 6:45](https://doi.org/10.1186/s13321-014-0045-3)

based on two already published constructive projects along this road: The SPICES molecular structure line notation

- [K. van den Broek, M. Daniel, M. Epple, H. Kuhn, J. Schaub and A. Zielesny, _SPICES: a particle-based molecular structure line notation and support library for mesoscopic simulation_, Journal of Cheminformatics (2018), 10:35](https://doi.org/10.1186/s13321-018-0294-7)
- [SPICES repository on GitHub](https://github.com/zielesny/SPICES)

and the Jdpd simulation kernel for Molecular Fragment (Dissipative Particle) Dynamics:

- [K. van den Broek, H. Kuhn and A. Zielesny, _Jdpd - An open Java Simulation Kernel for Molecular Fragment Dissipative Particle Dynamics_, Journal of Cheminformatics (2018), 10:25](https://doi.org/10.1186/s13321-018-0278-7)
- [Jdpd repository on GitHub](https://github.com/zielesny/Jdpd)

Additionally used open projects:

- [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/), [Apache Commons RNG](http://commons.apache.org/proper/commons-rng/), [Apache Commons Math](https://commons.apache.org/proper/commons-math/), [BioJava](http://biojava.org/), [GraphStream](http://graphstream-project.org/), [Java Matrix Package](http://math.nist.gov/javanumerics/jama/), [JCommon](http://www.jfree.org/jcommon/), [JDOM](http://www.jdom.org/), [JFreeChart](http://www.jfree.org/jfreechart/), [Jmol](http://jmol.sourceforge.net/), [3D Vector Math Package](https://mvnrepository.com/artifact/javax.vecmath/vecmath), [FFmpeg (Static) Version](https://ffmpeg.org)

### Acknowledgements

The support of [CAM-D Technologies GmbH](http://www.molecular-dynamics.de) and [GNWI - Gesellschaft für naturwissenschaftliche Informatik mbH](http://www.gnwi.de) is gratefully acknowledged.
