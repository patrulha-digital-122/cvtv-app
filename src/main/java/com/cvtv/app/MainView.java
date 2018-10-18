package com.cvtv.app;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.external.org.slf4j.Logger;
import com.vaadin.external.org.slf4j.LoggerFactory;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout
{
	private static final long					serialVersionUID	= -8783462098490997667L;
	private final Logger						logger				= LoggerFactory.getLogger( getClass() );
	private final List< Person >				data				= new ArrayList<>();
	private final VerticalLayout				gridVerticalLayout	= new VerticalLayout();
	private final Grid< Person >				grid				= new Grid<>( Person.class );
	private final MemoryBuffer					fileBuffer			= new MemoryBuffer();
	public static final Map< Integer, String >	headerRowMap		= new HashMap<>();
	private final TextArea						textArea			= new TextArea();
	private final TextField						emailText			= new TextField( "E-mail", "E-mail" );
	private final TextField						nomeText			= new TextField( "Nome", "Nome" );

	public MainView( @Autowired MessageBean bean )
	{
		final Button button = new Button( "Click me", e -> showEmailsWindow() );
		button.setSizeFull();

		final Upload upload = new Upload( fileBuffer );
		upload.setMaxFiles( 1 );

		gridVerticalLayout.setSizeFull();
		textArea.setSizeFull();
		emailText.setValue( "E-mail" );
		emailText.setWidth( "100%" );
		nomeText.setValue( "Nome" );
		nomeText.setWidth( "100%" );
		upload.addSucceededListener( event ->
		{
			logger.info( "Succeded: " + fileBuffer.getFileData().getFileName() );
			updateGrid();
		} );

		setAlignItems( Alignment.CENTER );
		add( upload, grid, button );
	}

	private Object showEmailsWindow()
	{
		final Dialog dialog = new Dialog();

		final VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setMargin( false );
		verticalLayout.setSizeFull();
		final ValueChangeListener< ValueChangeEvent< ? > > changeListener = event -> updateTextArea( grid.getSelectedItems() );
		emailText.addValueChangeListener( changeListener );
		nomeText.addValueChangeListener( changeListener );
		updateTextArea( grid.getSelectedItems() );
		verticalLayout.add( emailText, nomeText, textArea );
		dialog.add( verticalLayout );
		dialog.setWidth( "400px" );
		dialog.setHeight( "600px" );

		dialog.open();
		return null;
	}

	private void updateTextArea( Set< Person > selectedItems )
	{
		final StringBuilder sb = new StringBuilder();
		for ( final Person person : selectedItems )
		{
			if ( sb.length() > 0 )
			{
				sb.append( ", " );
			}
			// if ( chbWithNames.getValue() )
			// {
			sb.append( "\"" );
			sb.append( person.getValue( nomeText.getValue() ) );
			sb.append( "\" " );
			// }
			sb.append( "<" );
			sb.append( person.getValue( emailText.getValue() ) );
			sb.append( ">" );
		}

		textArea.setValue( sb.toString() );

	}

	private void updateGrid()
	{
		data.clear();
		headerRowMap.clear();

		try ( final Workbook workbook = new XSSFWorkbook( fileBuffer.getInputStream() ) )
		{
			final Sheet datatypeSheet = workbook.getSheetAt( 0 );
			final Iterator< Row > iterator = datatypeSheet.iterator();
			if ( iterator.hasNext() )
			{
				final Row headerRow = iterator.next();

				final Iterator< Cell > cellIterator = headerRow.cellIterator();
				while ( cellIterator.hasNext() )
				{
					final Cell cell = cellIterator.next();
					final String strNormalize =
									Normalizer.normalize( cell.getStringCellValue(), Normalizer.Form.NFD ).replaceAll( "[^\\p{ASCII}]", "" );
					headerRowMap.put( cell.getColumnIndex(), strNormalize );
				}
			}

			while ( iterator.hasNext() )
			{
				final Row row = iterator.next();
				final Iterator< Cell > cellIterator = row.cellIterator();
				final Person person = new Person();
				while ( cellIterator.hasNext() )
				{
					final Cell cell = cellIterator.next();
					switch ( cell.getCellType() )
					{
						case BLANK:
							person.setValue( headerRowMap.get( cell.getColumnIndex() ), "" );
							break;
						case STRING:
						case FORMULA:
							person.setValue( headerRowMap.get( cell.getColumnIndex() ), cell.getStringCellValue() );
							break;
						case BOOLEAN:
							person.setValue( headerRowMap.get( cell.getColumnIndex() ), String.valueOf( cell.getBooleanCellValue() ) );
							break;
						case NUMERIC:
							person.setValue( headerRowMap.get( cell.getColumnIndex() ), String.valueOf( cell.getNumericCellValue() ) );
							break;
						default:
							person.setValue( headerRowMap.get( cell.getColumnIndex() ), "?" );
							break;
					}
				}
				data.add( person );
			}
			grid.setSelectionMode( SelectionMode.MULTI );
			grid.setItems( data );
			if ( !headerRowMap.isEmpty() )
			{

				headerRowMap.values().forEach( col ->
				{
					// grid.removeColumnByKey( col );
					grid.addColumn( source -> source.getValue( col ) ).setHeader( col );
				} );
			}
			headerRowMap.values().forEach( p -> grid.addColumn( p ) );

			gridVerticalLayout.removeAll();
			gridVerticalLayout.add( grid );

		}
		catch ( final Exception e )
		{
			logger.error( e.getMessage(), e );
		}
	}

}
