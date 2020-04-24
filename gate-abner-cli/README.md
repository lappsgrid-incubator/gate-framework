# GATE Abner Tagger Command Line Interface

The project provides an executable jar file that can process single files or can be used to recursively process directories of files.

## Synopsis

``` 
$> java -jar abner.jar [-i <format>] [-o <format>] <input_file_or_directory> [<output>]
```

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