package com.turtle;

// Import required libraries
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import uk.ac.leedsbeckett.oop.LBUGraphics;

public class TurtleGraphics extends LBUGraphics {
    private JFrame mainFrame; // Main application window
    private JTextArea commandHistoryArea;// Text area to display command history and output
    private boolean imageSaved = true;// Flag to track whether the current drawing has been saved
    
   
  //Constructor - initializes the turtle graphics application
    public TurtleGraphics() {
        
        mainFrame = new JFrame("Turtle Graphics Application");
       
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create and configure menu bar
        createMenuBar();
        
        // Set up main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        // Add turtle graphics canvas to center
        mainPanel.add(this, BorderLayout.CENTER);
        
        // Initialize command history text area
        commandHistoryArea = new JTextArea(5, 30);
        commandHistoryArea.setEditable(false); // Make it read-only
        
        // Add scrolling capability to command history
        JScrollPane scrollPane = new JScrollPane(commandHistoryArea);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        
        // Add main panel to frame and display
        mainFrame.add(mainPanel);
        mainFrame.pack(); // Size frame to components
        mainFrame.setVisible(true);
        
        // Initialize turtle state
        reset(); 
        setPenColour(Color.red); 
        setStroke(2); 
        setPenState(true); 
        
        // Display welcome messages
        displayMessage("Welcome to Turtle Graphics! Type 'help' for commands.");
        appendToCommandHistory("Application started. Type 'help' for commands.");
    }
    
    // Call parent's about method
    public void superAbout() {
        super.about();
    }
    
    //Check if a position is within canvas bounds 
    private boolean isPositionValid(int x, int y) {
        return x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight();
    }
    //Check if a movement distance is reasonable
     private boolean isDistanceValid(int distance) {
        return Math.abs(distance) <= 1000;
    }
    //Check if an angle is valid
     private boolean isAngleValid(int angle) {
        return angle >= -360 && angle <= 360;
    }
    
    // Check if a shape size/radius is valid
    private boolean isSizeValid(int size) {
        return size > 0 && size <= 500;
    }

    // ========== UTILITY METHODS ==========
    
    //Append text to the command history area
    private void appendToCommandHistory(String command) {
        commandHistoryArea.append(command + "\n");
    }

    // Create and configure the menu bar with all menu items
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // ===== File Menu =====
        JMenu fileMenu = new JMenu("File");
        
        // --- Save Submenu ---
        JMenu saveMenu = new JMenu("Save");
        JMenuItem saveImageItem = new JMenuItem("Save as Image");
        JMenuItem saveTextItem = new JMenuItem("Save Commands as Text");
        
        // Save image action
        saveImageItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImageFile();
            }
        });
        
        // Save text action
        saveTextItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTextFile();
            }
        });
        
        saveMenu.add(saveImageItem);
        saveMenu.add(saveTextItem);
        
        // --- Load Submenu ---
        JMenu loadMenu = new JMenu("Load");
        JMenuItem loadImageItem = new JMenuItem("Load Image");
        JMenuItem loadTextItem = new JMenuItem("Load Commands from Text");
        
        // Load image action
        loadImageItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImageFile();
            }
        });
        
        // Load text action
        loadTextItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTextFile();
            }
        });
        
        loadMenu.add(loadImageItem);
        loadMenu.add(loadTextItem);
        
        // --- Exit Item ---
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Terminate application
            }
        });
        
        // Add all items to File menu
        fileMenu.add(saveMenu);
        fileMenu.add(loadMenu);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // ===== Help Menu =====
        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpItem = new JMenuItem("Commands Help");
       
        // Help action
        helpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });
        
        helpMenu.add(helpItem);
        
        // ===== Canvas Menu =====
        JMenu CanvasMenu = new JMenu("Canvas");
        JMenuItem clearCanvasItem = new JMenuItem("Clear Canvas");
        JMenuItem ResetCanvasItem = new JMenuItem("Reset Canvas");
        
        // Clear canvas action
        clearCanvasItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Warn if unsaved changes exist
                if (!imageSaved) {
                    int option = JOptionPane.showConfirmDialog(mainFrame, 
                        "Current image is not saved. Clear anyway?", 
                        "Warning", 
                        JOptionPane.YES_NO_OPTION);
                    if (option != JOptionPane.YES_OPTION) {
                        appendToCommandHistory("Clear operation cancelled via menu");
                        return;
                    }
                }
                clear(); // Clear canvas
                displayMessage("Canvas cleared");
                appendToCommandHistory("Canvas cleared via menu");
                imageSaved = false;
            }
        });
        
        // Reset canvas action
        ResetCanvasItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Warn if unsaved changes exist
                if (!imageSaved) {
                    int option = JOptionPane.showConfirmDialog(mainFrame, 
                        "Current image is not saved. Reset anyway?", 
                        "Warning", 
                        JOptionPane.YES_NO_OPTION);
                    if (option != JOptionPane.YES_OPTION) {
                        appendToCommandHistory("Reset operation cancelled via menu");
                        return;
                    }
                }
                reset(); // Reset turtle
                displayMessage("Canvas Reset");
                appendToCommandHistory("Canvas Reset via menu");
                imageSaved = false;
            }
        });
        
        CanvasMenu.add(clearCanvasItem);
        CanvasMenu.add(ResetCanvasItem);
         
        // ===== Add All Menus to Menu Bar =====
        menuBar.add(fileMenu);
        menuBar.add(CanvasMenu);
        menuBar.add(helpMenu);
        
        // Set menu bar on main frame
        mainFrame.setJMenuBar(menuBar);
    }
    
    // ========== FILE OPERATIONS ==========
    
    // Save current drawing as an image file
    private void saveImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image As");
        fileChooser.setSelectedFile(new File("turtle_drawing.png")); // Default filename
        
        // Show save dialog
        int userSelection = fileChooser.showSaveDialog(mainFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                // Get current drawing as image
                BufferedImage image = getBufferedImage();
                // Write PNG file
                ImageIO.write(image, "PNG", fileToSave);
                displayMessage("Drawing saved as " + fileToSave.getName());
                appendToCommandHistory("Saved image to: " + fileToSave.getName());
                imageSaved = true; // Update saved state
            } catch (Exception e) {
                // Handle save errors
                displayMessage("Error saving image: " + e.getMessage());
                appendToCommandHistory("Error saving image: " + e.getMessage());
            }
        }
    }
  
    //Load image file and display as drawing
    private void loadImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Image");
        
        // Show open dialog
        int userSelection = fileChooser.showOpenDialog(mainFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try {
                // Read image file
                BufferedImage image = ImageIO.read(fileToLoad);
                // Set as current drawing
                setBufferedImage(image);
                displayMessage("Drawing loaded from " + fileToLoad.getName());
                appendToCommandHistory("Loaded image from: " + fileToLoad.getName());
                imageSaved = true; // Update saved state
            } catch (Exception e) {
                // Handle load errors
                displayMessage("Error loading image: " + e.getMessage());
                appendToCommandHistory("Error loading image: " + e.getMessage());
            }
        }
    }
    
    // Save command history as text file
    private void saveTextFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Commands As");
        fileChooser.setSelectedFile(new File("turtle_commands.txt")); // Default filename
        
        // Show save dialog
        int userSelection = fileChooser.showSaveDialog(mainFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(fileToSave)) {
                // Write command history to file
                writer.println(commandHistoryArea.getText());
                displayMessage("Command history saved as " + fileToSave.getName());
                appendToCommandHistory("Saved command history to: " + fileToSave.getName());
            } catch (Exception e) {
                // Handle save errors
                displayMessage("Error saving text file: " + e.getMessage());
                appendToCommandHistory("Error saving command history: " + e.getMessage());
            }
        }
    }
    
 
     // Load commands from text file and execute them
    private void loadTextFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Commands");
        
        // Show open dialog
        int userSelection = fileChooser.showOpenDialog(mainFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileToLoad))) {
                String line;
                // Read file line by line
                while ((line = reader.readLine()) != null) {
                    // Skip empty lines and comments
                    if (!line.trim().isEmpty() && !line.trim().startsWith("//")) {
                        processCommand(line); // Execute each command
                    }
                }
                displayMessage("Commands loaded from " + fileToLoad.getName());
                appendToCommandHistory("Loaded commands from: " + fileToLoad.getName());
                imageSaved = false; // Mark as unsaved since we modified drawing
            } catch (Exception e) {
                // Handle load errors
                displayMessage("Error loading text file: " + e.getMessage());
                appendToCommandHistory("Error loading commands: " + e.getMessage());
            }
        }
    }

    // ========== TURTLE COMMANDS PROCESSING ==========
    

     //Process a turtle graphics command
    public void processCommand(String command) {
        // Add command to history
        appendToCommandHistory("> " + command);
        System.out.println("Processing command: " + command);
        
        try {
            // Split command into parts
            String[] parts = command.trim().split("\\s+");
            if (parts.length == 0 || parts[0].isEmpty()) return;
            
            // Get base command (lowercase for case-insensitive comparison)
            String cmd = parts[0].toLowerCase();
            
            // Process different commands
            switch(cmd) {
                case "forward":
                case "move":
                    if (parts.length == 2) {
                        try {
                            int distance = Integer.parseInt(parts[1]);
                            if (!isDistanceValid(distance)) {
                                displayMessage("Distance too large (max 1000 pixels)");
                                appendToCommandHistory("Error: Distance exceeds maximum allowed");
                                break;
                            }
                            
                            // Calculate new position
                            int newX = getxPos() + (int)(distance * Math.sin(Math.toRadians(getDirection())));
                            int newY = getyPos() - (int)(distance * Math.cos(Math.toRadians(getDirection())));
                            
                            if (!isPositionValid(newX, newY)) {
                                displayMessage("Cannot move turtle off screen");
                                appendToCommandHistory("Error: Movement would take turtle off screen");
                                break;
                            }
                            
                            forward(distance);
                            appendToCommandHistory("Moved forward by " + distance + " pixels");
                            imageSaved = false;
                        } catch (NumberFormatException e) {
                            displayMessage("Enter a valid Numeric Parameter!");
                            appendToCommandHistory("Error: Invalid Parameter");
                        }
                    } else {
                        displayMessage(" Missing Paramenter!");
                        appendToCommandHistory("Error: Missing Parameter");
                    }
                    break;
                    
                case "name":
                    about();
                    appendToCommandHistory("Name Drawn");
                    imageSaved = false;
                    break;
            
                case "about":
                    superAbout();
                    break;
                    
                case "backward":
                case "reverse":
                    if (parts.length == 2) {
                        try {
                            int distance = Integer.parseInt(parts[1]);
                            if (!isDistanceValid(distance)) {
                                displayMessage("Distance too large (max 1000 pixels)");
                                appendToCommandHistory("Error: Distance exceeds maximum allowed");
                                break;
                            }
                            
                            // Calculate new position for backward movement
                            int newX = getxPos() - (int)(distance * Math.sin(Math.toRadians(getDirection())));
                            int newY = getyPos() + (int)(distance * Math.cos(Math.toRadians(getDirection())));
                            
                            if (!isPositionValid(newX, newY)) {
                                displayMessage("Cannot move turtle off screen");
                                appendToCommandHistory("Error: Movement would take turtle off screen");
                                break;
                            }
                            
                            forward(-distance); // Move backward
                            appendToCommandHistory("Moved backward by " + distance + " pixels");
                            imageSaved = false;
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid distance - must be integer");
                            appendToCommandHistory("Error: Invalid distance format");
                        }
                    } else {
                        displayMessage("Syntax: backward <distance>");
                        appendToCommandHistory("Error: Syntax: backward <distance>");
                    }
                    break;
                    
                case "right":
                    if (parts.length == 2) {
                        try {
                            int angle = Integer.parseInt(parts[1]);
                            if (!isAngleValid(angle)) {
                                displayMessage("Angle must be between -360 and 360 degrees");
                                appendToCommandHistory("Error: Invalid angle specified");
                                break;
                            }
                            right(angle);
                            appendToCommandHistory("Turned right by " + angle + " degrees");
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid angle - must be integer");
                            appendToCommandHistory("Error: Invalid angle format");
                        }
                    } else {
                        right(90); // Default 90 degree turn
                        appendToCommandHistory("Turned right by 90 degrees");
                    }
                    break;
                    
                case "left":
                    if (parts.length == 2) {
                        try {
                            int angle = Integer.parseInt(parts[1]);
                            if (!isAngleValid(angle)) {
                                displayMessage("Angle must be between -360 and 360 degrees");
                                appendToCommandHistory("Error: Invalid angle specified");
                                break;
                            }
                            left(angle);
                            appendToCommandHistory("Turned left by " + angle + " degrees");
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid angle - must be integer");
                            appendToCommandHistory("Error: Invalid angle format");
                        }
                    } else {
                        left(90); // Default 90 degree turn
                        appendToCommandHistory("Turned left by 90 degrees");
                    }
                    break;
                    
                case "penwidth":
                    if (parts.length == 2) {
                        try {
                            setStroke(Integer.parseInt(parts[1]));
                            displayMessage("Pen width set to " + parts[1]);
                            appendToCommandHistory("Set pen width to " + parts[1]);
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid width - must be integer");
                            appendToCommandHistory("Error: Invalid width - must be integer");
                        }
                    } else {
                        displayMessage("Syntax: penwidth <size>");
                        appendToCommandHistory("Error: Syntax: penwidth <size>");
                    }
                    break;
                    
                case "penup":
                case "pu":
                    setPenState(false);
                    displayMessage("Pen lifted");
                    appendToCommandHistory("Pen lifted");
                    break;
                    
                case "pendown":
                case "pd":
                    setPenState(true);
                    displayMessage("Pen lowered");
                    appendToCommandHistory("Pen lowered");
                    break;

                case "pencolour":
                    if (parts.length == 2) {
                        // Handle color name
                        String colorName = parts[1].toLowerCase();
                        setPenColour(colorName); // Uses the named color method
                    } else if (parts.length == 4) {
                        // Handle RGB values
                        try {
                            setpencolour(
                                Integer.parseInt(parts[1]),
                                Integer.parseInt(parts[2]),
                                Integer.parseInt(parts[3])
                            );
                            displayMessage("Pen color set to RGB(" + parts[1] + "," + parts[2] + "," + parts[3] + ")");
                            appendToCommandHistory("Set pen color to RGB(" + parts[1] + "," + parts[2] + "," + parts[3] + ")");
                        } catch (Exception e) {
                            displayMessage("Need 3 numbers (0-255)");
                            appendToCommandHistory("Error: Need 3 numbers (0-255) for RGB");
                        }
                    } else {
                        displayMessage("Syntax: pencolour <colorName> OR pencolour <r> <g> <b>");
                        appendToCommandHistory("Error: Syntax: pencolour <colorName> OR pencolour <r> <g> <b>");
                    }
                    break;
                    
                case "clear":
                    // Warn if unsaved changes exist
                    if (!imageSaved) {
                        int option = JOptionPane.showConfirmDialog(mainFrame, 
                            "Current image is not saved. Clear anyway?", 
                            "Warning", 
                            JOptionPane.YES_NO_OPTION);
                        if (option != JOptionPane.YES_OPTION) {
                            appendToCommandHistory("Clear operation cancelled");
                            break;
                        }
                    }
                    clear();
                    displayMessage("Canvas cleared");
                    appendToCommandHistory("Canvas cleared");
                    imageSaved = false;
                    break;
                    
                case "reset":
                    // Warn if unsaved changes exist
                    if (!imageSaved) {
                        int option = JOptionPane.showConfirmDialog(mainFrame, 
                            "Current image is not saved. Reset anyway?", 
                            "Warning", 
                            JOptionPane.YES_NO_OPTION);
                        if (option != JOptionPane.YES_OPTION) {
                            appendToCommandHistory("Reset operation cancelled");
                            break;
                        }
                    }
                    reset();
                    displayMessage("Turtle reset");
                    appendToCommandHistory("Turtle reset");
                    imageSaved = false;
                    break;
                    
                case "circle":
                    if (parts.length == 2) {
                        try {
                            int radius = Integer.parseInt(parts[1]);
                            if (!isSizeValid(radius)) {
                                displayMessage("Radius must be between 1 and 500 pixels");
                                appendToCommandHistory("Error: Invalid radius size");
                                break;
                            }
                            circle(radius);
                            appendToCommandHistory("Drawn circle with radius " + radius);
                            imageSaved = false;
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid radius - must be integer");
                            appendToCommandHistory("Error: Invalid radius format");
                        }
                    } else {
                        displayMessage("Syntax: circle <radius>");
                        appendToCommandHistory("Error: Syntax: circle <radius>");
                    }
                    break;
                    
                case "square":
                    if (parts.length == 2) {
                        try {
                            int size = Integer.parseInt(parts[1]);
                            if (!isSizeValid(size)) {
                                displayMessage("Size must be between 1 and 500 pixels");
                                appendToCommandHistory("Error: Invalid square size");
                                break;
                            }
                            drawSquare(size);
                            appendToCommandHistory("Drawn square with size " + size);
                            imageSaved = false;
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid size - must be integer");
                            appendToCommandHistory("Error: Invalid size format");
                        }
                    } else {
                        displayMessage("Syntax: square <size>");
                        appendToCommandHistory("Error: Syntax: square <size>");
                    }
                    break;
                    
                case "triangle": 
                    if (parts.length < 2) {
                        // No parameters provided
                        displayMessage("Syntax: triangle <size> OR triangle <side1> <side2> <side3>");
                        appendToCommandHistory("Error: Missing parameters for triangle");
                    } else {
                        try {
                            // Case 1: Single parameter (equilateral triangle)
                            if (parts.length == 2) {
                                int size = Integer.parseInt(parts[1]);
                                if (!isSizeValid(size)) {
                                    displayMessage("Size must be between 1 and 500 pixels");
                                    appendToCommandHistory("Error: Invalid triangle size");
                                    break;
                                }
                                equilateralTriangle(size);
                            }
                            // Case 2: Three parameters (scalene triangle)
                            else if (parts.length == 4) {
                                int a = Integer.parseInt(parts[1]);
                                int b = Integer.parseInt(parts[2]);
                                int c = Integer.parseInt(parts[3]);
                                
                                if (!isSizeValid(a) || !isSizeValid(b) || !isSizeValid(c)) {
                                    displayMessage("Sides must be between 1 and 500 pixels");
                                    appendToCommandHistory("Error: Invalid triangle side length");
                                    break;
                                } else if (!isValidTriangle(a, b, c)) {
                                    displayMessage("Invalid triangle - sum of any two sides must be greater than the third");
                                    appendToCommandHistory("Error: Invalid triangle sides provided");
                                    break;
                                } else {
                                    drawTriangle(a, b, c);
                                }
                            }
                            else {
                                displayMessage("Syntax: triangle <size> OR triangle <side1> <side2> <side3>");
                                appendToCommandHistory("Error: Invalid triangle command syntax");
                            }
                        } catch (NumberFormatException e) {
                            displayMessage("Invalid size - must be integer");
                            appendToCommandHistory("Error: Invalid size format");
                        }
                    }
                    break;
                    
                case "save":
                    if (parts.length == 2) {
                        saveDrawing(parts[1]);
                        appendToCommandHistory("Saved drawing as " + parts[1]);
                    } else {
                        displayMessage("Syntax: save <filename>");
                        appendToCommandHistory("Error: Syntax: save <filename>");
                    }
                    break;
                    
                case "load":
                    if (parts.length == 2) {
                        loadDrawing(parts[1]);
                        appendToCommandHistory("Loaded drawing from " + parts[1]);
                    } else {
                        displayMessage("Syntax: load <filename>");
                        appendToCommandHistory("Error: Syntax: load <filename>");
                    }
                    break;
                    
                case "olympics":
                    drawOlympicsLogo();
                    appendToCommandHistory("Olympics logo Drawn");
                    imageSaved = false;
                    break;
                    
                case "setspeed":
                    if (parts.length == 2) {
                        setTurtleSpeed(Integer.parseInt(parts[1]));
                        displayMessage("Speed set to " + parts[1]);
                        appendToCommandHistory("Set speed to " + parts[1]);
                    } else {
                        displayMessage("Syntax: setspeed <1-10>");
                        appendToCommandHistory("Error: Syntax: setspeed <1-10>");
                    }
                    break;
                    
                case "help":
                    showHelp();
                    appendToCommandHistory("Displayed help");
                    break;
                    
                default:
                    // Unknown command handling
                    displayMessage("Invalid Command: " + cmd);
                    appendToCommandHistory("Error: Invalid Command: " + cmd);
            }
        } catch (NumberFormatException e) {
            displayMessage("Please enter a valid number");
            appendToCommandHistory("Error: Please enter a valid number");
        } catch (ArrayIndexOutOfBoundsException e) {
            displayMessage("Missing parameter for command");
            appendToCommandHistory("Error: Missing parameter for command");
        } catch (Exception e) {
            displayMessage("Error: " + e.getMessage());
            appendToCommandHistory("Error: " + e.getMessage());
        }
    }
    
    // ========== DRAWING METHODS ==========
    
     // Set pen color using color name
    private void setPenColour(String colorName) {
        switch(colorName.toLowerCase()) {
            case "red":     setPenColour(Color.RED); break;
            case "green":   setPenColour(Color.GREEN); break;
            case "blue":    setPenColour(Color.BLUE); break;
            case "black":   setPenColour(Color.BLACK); break;
            case "yellow":  setPenColour(Color.YELLOW); break;
            case "cyan":    setPenColour(Color.CYAN); break;
            case "magenta": setPenColour(Color.MAGENTA); break;
            case "white":   setPenColour(Color.WHITE); break;
            case "gray":    setPenColour(Color.GRAY); break;
            default: 
                displayMessage("Available colors: red, green, blue, black, yellow, cyan, magenta, white, gray");
                return;
        }
        displayMessage("Pen color: " + colorName);
    }
    
     //Set pen color using RGB values
    private void setpencolour(int red, int green, int blue) {
        setPenColour(new Color(red, green, blue));
    }

     // Draw a square with given size
    private void drawSquare(int size) {
        setPenState(true);
        for (int i = 0; i < 4; i++) {
            forward(size);
            right(90);
        }
    }

     // Check if three sides can form a valid triangle
    private boolean isValidTriangle(int a, int b, int c) {
        return (a + b > c) && (a + c > b) && (b + c > a);
    }
   
     //Draw an equilateral triangle
    private void equilateralTriangle(int size) {
        setPenState(true);
        for (int i = 0; i < 3; i++) {
            forward(size);
            left(120);
        }
    }

  
     // Draw a triangle with specified side lengths
     private void drawTriangle(int side1, int side2, int side3) {
     // First validate if the sides can form a triangle
        if (side1 + side2 <= side3 || side1 + side3 <= side2 || side2 + side3 <= side1) {
            displayMessage("Invalid triangle - sum of any two sides must be greater than the third");
            appendToCommandHistory("Error: Invalid triangle sides provided");
            return;
        }
        
        setPenState(true);
        
        // Calculate angles using law of cosines
        double angleA = Math.acos((side2*side2 + side3*side3 - side1*side1) / (2.0 * side2 * side3));
        double angleB = Math.acos((side1*side1 + side3*side3 - side2*side2) / (2.0 * side1 * side3));
        double angleC = Math.PI - angleA - angleB;
        
        // Convert radians to degrees
        int angleADeg = (int) Math.toDegrees(angleA);
        int angleBDeg = (int) Math.toDegrees(angleB);
        int angleCDeg = (int) Math.toDegrees(angleC);
        
        // Draw the triangle
        forward(side1);
        left(180 - angleBDeg);
        forward(side2);
        left(180 - angleCDeg);
        forward(side3);
        
        // Return to original orientation
        left(180 - angleADeg);
    }
   
  
    //Save current drawing to file
    private void saveDrawing(String filename) {
        try {
            BufferedImage image = getBufferedImage();
            ImageIO.write(image, "PNG", new File(filename + ".png"));
            displayMessage("Drawing saved as " + filename + ".png");
            appendToCommandHistory("Drawing saved as " + filename + ".png");
            imageSaved = true;
        } catch (Exception e) {
            displayMessage("Error saving: " + e.getMessage());
            appendToCommandHistory("Error saving: " + e.getMessage());
        }
    }


     //Load drawing from file
     private void loadDrawing(String filename) {
        try {
            BufferedImage image = ImageIO.read(new File(filename));
            setBufferedImage(image);
            displayMessage("Drawing loaded from " + filename);
            appendToCommandHistory("Drawing loaded from " + filename);
            imageSaved = true;
        } catch (Exception e) {
            displayMessage("Error loading: " + e.getMessage());
            appendToCommandHistory("Error loading: " + e.getMessage());
        }
    }
       
    @Override
    //Custom about method that draws the AYUSHREE'S nsme
    public void about() {
        setTurtleSpeed(1);
        reset();
        setPenState(false);
        setPenColour(Color.MAGENTA);
        setStroke(3);
        
        right(90); 
        forward(250);
        right(90);
        
        setPenState(true);
        forward(100);
        right(90);
        forward(50);
        right(90);
        forward(100);
        forward(-50);
        right(90);
        forward(50);
        setPenState(false);
        
        setPenColour(Color.GREEN);
        forward(-50);
        left();
        forward(50);
        left();
        forward(40);
        left();
        setPenState(true);
        forward(50);
        left(30);
        forward(55);
        forward(-55);
        right(60);
        forward(55);
        forward(-55);
        
        setPenState(false);
        setPenColour(Color.RED);
        
        right(60);
        forward(40);
        setPenState(true);
        left(90);
        forward(-50);
        forward(100);
        forward(-100);
        right();
        forward(50);
        left();
        forward(100);
        
        forward(-100);
        setPenState(false);
        right();
        forward(30);
        left();
        forward(100);
        right();
        forward(30);
        left();
        left();
        setPenState(true);
        setPenColour(Color.BLUE);
        forward(40);
        left();
        forward(40);
        right();
        right();
        right();
        forward(40);
        right();
        forward(60);
        right();
        forward(45);
        
        setPenState(false);
        right();
        right();
        forward(60);
        
        setPenColour(Color.CYAN);
        left();
        setPenState(true);
        forward(100);
        right();
        right();
        setPenState(false);
        forward(50);
        left();
        setPenState(true);
        forward(40);
        left();
        forward(50);
        right();
        right();
        setPenState(false);
        forward(50);
        setPenState(true);
        forward(50);
        
        setPenState(false);
        left();
        forward(20);
        left();
        
        setPenState(true);
        setPenColour(Color.YELLOW);
        forward(100);
        right(90);
        forward(25);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        right(20);
        forward(10);
        left(140);
        forward(70);
        setPenState(false);

        left(40);
        forward(45);
        left();
        forward(93);
        left();
        setPenState(true);    
        setPenColour(Color.WHITE);
        forward(47);
        left();
        forward(44);
        left();
        forward(47);
        
        setPenState(false);    
        left();
        left();
  
        forward(47);
        left();
      
        setPenState(true);
        forward(51);
        left();
        forward(47);
      
        setPenState(false);
        forward(28);
        left();
        forward(93);
      
        right();
        forward(47);
        left();
        left();
        setPenState(true);
        forward(50);
        left();
        forward(47);
        left();
        forward(50);
        left();
        left();
        setPenState(false);
        forward(50);
        left();
      
        setPenState(true);
        forward(47);
        left();
        forward(50);
    }
     
 // Draw Olympic logo
     public void drawOlympicsLogo() {
        setTurtleSpeed(1);
        reset();
        setPenState(false);
        setStroke(5);
        
        setPenState(false);
        right();
        forward(200);
        right();
        
        forward(30);
        
        setPenColour(Color.BLUE);
        setPenState(true);
        circle(90);
        setPenState(false);
        
        right();
        
        forward(120);
        left();
        left();
        forward(-80);
        setPenState(true);
        setPenColour(Color.GRAY);
        circle(90);
        
        setPenState(false);
        right();
        right();
        
        forward(200);
        left();
        left();
        setPenState(true);
        setPenColour(Color.RED);
        circle(90);
        
        setPenState(false);
        forward(100);
        left();
        forward(90);
        right();
        right();
        setPenState(true);
        setPenColour(Color.GREEN);
        circle(90);
       
        left();
        setPenState(false); 
        
        forward(210);
        right();
        setPenState(true); 
        setPenColour(Color.YELLOW);
        circle(90);
        
        setPenState(false); 
        forward(-30);
        
        left();
        forward(220);
        
        left();
    }
    
    // Display help information in a dialog box
    private void showHelp() {
        StringBuilder helpText = new StringBuilder();
        helpText.append("=== Turtle Graphics Commands ===\n\n");
        
            helpText.append("Movement:\n");
            helpText.append("  forward <n> or move <n> - Move forward n pixels\n");
            helpText.append("  backward <n> or reverse <n> - Move backward n pixels\n");
            helpText.append("  left <degrees> - Turn left by specified degrees (default 90)\n");
            helpText.append("  right <degrees> or rt <degrees> - Turn right by specified degrees (default 90)\n");
            helpText.append("  setspeed <1-10> - Set animation speed (1=slowest, 10=fastest)\n\n");
            
            helpText.append("Pen Control:\n");
            helpText.append("  penup or pu - Lift pen (stop drawing)\n");
            helpText.append("  pendown or pd - Lower pen (start drawing)\n");
            helpText.append("  penwidth <size> - Set pen thickness in pixels\n");
            helpText.append("  red/green/blue/black - Set pen color to specified color\n");
            helpText.append("  pen <r> <g> <b> - Set custom RGB color (0-255 for each)\n\n");
            
            helpText.append("Shapes:\n");
            helpText.append("  circle <radius> - Draw circle with given radius\n");
            helpText.append("  square <size> - Draw square with given side length\n");
            helpText.append("  triangle <size> - Draw equilateral triangle\n");
            helpText.append("  triangle <side1> <side2> <side3> - Draw custom triangle\n");
            helpText.append("  olympics - Draw Olympic rings logo\n");
            helpText.append("  name - Draw your name\n\n");
            
            helpText.append("Canvas Control:\n");
            helpText.append("  clear - Clear the canvas (with warning if unsaved)\n");
            helpText.append("  reset - Reset turtle to center position\n");
            helpText.append("  about - Show program information and animation\n\n");
            
            helpText.append("File Operations:\n");
            helpText.append("  save <filename> - Save drawing as PNG image\n");
            helpText.append("  load <filename> - Load image file\n");
            helpText.append("  (Menu options also available for saving/loading)\n\n");
            
            helpText.append("Special Features:\n");
            helpText.append("  help - Show this help message\n");
            helpText.append("  Menu options for advanced file operations\n");
            helpText.append("  Command history tracking\n");
            helpText.append("  Unsaved changes warnings\n\n");
            
            helpText.append("Bounds and Limits:\n");
            helpText.append("  - Movement distance limited to 1000 pixels\n");
            helpText.append("  - Angles limited to -360 to 360 degrees\n");
            helpText.append("  - Shape sizes limited to 500 pixels\n");
            helpText.append("  - Turtle cannot move off screen\n\n");
            
            helpText.append("Examples:\n");
            helpText.append("  move 100 - Move forward 100 pixels\n");
            helpText.append("  right 45 - Turn right 45 degrees\n");
            helpText.append("  pen 255 0 0 - Set pen to red (RGB)\n");
            helpText.append("  square 50 - Draw square with 50px sides\n");
            helpText.append("  triangle 60 80 100 - Draw custom triangle\n");

        
        JTextArea textArea = new JTextArea(helpText.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        
        JOptionPane.showMessageDialog(
            mainFrame,
            scrollPane,
            "Turtle Graphics Help - Complete Command Reference",
            JOptionPane.INFORMATION_MESSAGE
        ); 
        appendToCommandHistory("Displayed complete help dialog");
    }
}