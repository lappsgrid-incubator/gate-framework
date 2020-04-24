# GATE Abner Tagger Command Line Interface

The project provides an executable jar file that can process single files or can be used to recursively process directories of files.

## Synopsis

``` 
$> java -jar abner.jar [-i <format>] [-o <format>] <input_file_or_directory> [<output>]
```

## Setup

If you do not have already GATE installed you can download the required files from https://downloads.lappsgrid.org/gate_abner.tgz

``` 
$> wget https://downloads.lappsgrid.org/gate_abner.tgz
$> tar -xzf gate_abner.tgz --directory /usr/local
$> export GATE_HOME=/usr/local/gate_abner
$> java -jar abner.jar --help
```
You can, of course, extract `gate_abner.tgz` anywhere you want as long as `$GATE_HOME` is set appropriately.

If you already have GATE installed with the AbnerTagger plugin available just ensure `$GATE_HOME` is set or that GATE can be found in one of the default locations [`/usr/local/gate`, `/home/gate`, `/gate`].

## Supported Formats

By default the application expects the input to be plain text (UTF-8) and will write LIF output.  These can be changed with the `--input-format` (`-i`) and `--output-format` (`-o`) options.

### Input Formats

- txt (default)
- gate
- lif
- cord

### Output Formats

- lif (default)
- gate


## Examples

#### Writes LIF to stdout.
```
$> java -jar abner.jar input.txt
$> java -jar abner.jar -i lif input.lif
```

#### Write LIF output to `/tmp/output.lif`
``` 
$> java -jar abner.jar input.txt /tmp/output.lif
$> java -jar abner.jar -i gate input.xml /tmp/output.lif
``` 

#### Write LIF output to `/tmp/output_directory/input.txt.lif`
``` 
$> java -jar abner.jar input.txt /tmp/output_directory/
```

#### Recursively process all files in `/input/directory/`
Output files will be written to `/output/directory/` recursively creating directories to match the directory structure of `/input/directory`

``` 
$java -jar abner.jar /input/directory/ /output/directory/
```