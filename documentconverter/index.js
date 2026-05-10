document.addEventListener("DOMContentLoaded", () => {
  const theme = ECTheme.Green;
  const app = document.getElementById("app");
  
  const topbar = new ECTopbar("Document Converter").setTheme(theme);
  topbar.element.classList.add("background-var(--ec-accent,_#2e7d32)");
  topbar._titleEl.classList.replace("color-var(--ec-text,_#212529)", "color-#ffffff");
  app.appendChild(topbar.element);
  const container = document.createElement("div");
  container.className = "padding-100px_20px_40px maxWidth-700px margin-0_auto display-flex flexDirection-column gap-24px";
  app.appendChild(container);
  const headerText = document.createElement("div");
  headerText.className = "textAlign-center color-#1b4332 marginBottom-8px";
  headerText.innerHTML = `
    <h2 class="fontSize-28px fontWeight-800 margin-0">File Conversion Utility</h2>
    <p class="fontSize-15px color-#52796f marginTop-8px">Instantly convert PDFs to Word/Text, or Word to PDF right in your browser securely.</p>
  `;
  container.appendChild(headerText);
  const card = new ECBasicCard();
  container.appendChild(card.element);
  const modeDropdown = new ECDropdown({
    label: "Select Conversion Mode",
    items:[
      { value: "pdf-to-docx", label: "PDF to Document (DOCX)" },
      { value: "pdf-to-txt", label: "PDF to Plain Text (TXT)" },
      { value: "docx-to-pdf", label: "Document (DOCX) to PDF" }
    ]
  }).setTheme(theme);
  card.append(modeDropdown);
  const spacer1 = document.createElement("div");
  spacer1.className = "height-20px";
  card.append(spacer1);
  const fileUpload = new ECFileUpload({
    title: "Drag & Drop File",
    accept: ".pdf"
  }).setTheme(theme);
  card.append(fileUpload);
  const spacer2 = document.createElement("div");
  spacer2.className = "height-24px";
  card.append(spacer2);
  const progress = new ECProgressBar({ label: "Processing file...", value: 0 }).setTheme(theme);
  progress.element.style.display = "none";
  card.append(progress);
  const spacer3 = document.createElement("div");
  spacer3.className = "height-16px";
  card.append(spacer3);
  const convertBtn = new ECButton("Start Conversion").setTheme(theme);
  convertBtn.element.classList.add("width-100%", "padding-14px");
  card.append(convertBtn);
  modeDropdown.onChange((val) => {
    fileUpload.clear();
    if (val.startsWith("pdf")) {
      fileUpload._input.accept = ".pdf";
      fileUpload._accept = ".pdf";
    } else {
      fileUpload._input.accept = ".docx";
      fileUpload._accept = ".docx";
    }
  });
  function downloadFileBlob(blob, fileName) {
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }
  async function extractTextFromPdf(file) {
    const arrayBuffer = await file.arrayBuffer();
    const pdf = await pdfjsLib.getDocument({ data: arrayBuffer }).promise;
    let fullText = "";
    const totalPages = pdf.numPages;
    
    for (let i = 1; i <= totalPages; i++) {
      const page = await pdf.getPage(i);
      const textContent = await page.getTextContent();
      const pageText = textContent.items.map(item => item.str).join(" ");
      fullText += pageText + "\n\n";
      progress.setValue(10 + Math.floor((i / totalPages) * 40));
    }
    return fullText;
  }
  convertBtn.onClick(async () => {
    const files = fileUpload.getFiles();
    if (files.length === 0) {
      new ECToast("Please upload a file first.", { type: "warning" }).show();
      return;
    }
    const file = files[0];
    const mode = modeDropdown.getValue();
    if (mode.startsWith("pdf") && !file.name.toLowerCase().endsWith(".pdf")) {
      new ECToast("Please upload a valid PDF file.", { type: "error" }).show();
      return;
    } else if (mode.startsWith("docx") && !file.name.toLowerCase().endsWith(".docx")) {
      new ECToast("Please upload a valid DOCX file.", { type: "error" }).show();
      return;
    }
    convertBtn.disable();
    progress.element.style.display = "flex";
    progress.setValue(10);
    try {
      if (mode === "pdf-to-txt") {
        const text = await extractTextFromPdf(file);
        progress.setValue(80);
        const blob = new Blob([text], { type: "text/plain" });
        downloadFileBlob(blob, file.name.replace(/\.pdf$/i, ".txt"));
        
      } else if (mode === "pdf-to-docx") {
        const text = await extractTextFromPdf(file);
        progress.setValue(60);
        const paragraphs = text.split("\n").map(line => 
          new docx.Paragraph({
            children: [new docx.TextRun(line || " ")] // fallback space to keep blank lines
          })
        );
        const doc = new docx.Document({
          sections:[{
            properties: {},
            children: paragraphs
          }]
        });
        progress.setValue(80);
        const blob = await docx.Packer.toBlob(doc);
        downloadFileBlob(blob, file.name.replace(/\.pdf$/i, ".docx"));
        
      } else if (mode === "docx-to-pdf") {
        const arrayBuffer = await file.arrayBuffer();
        progress.setValue(40);
        const result = await mammoth.extractRawText({ arrayBuffer: arrayBuffer });
        const text = result.value;
        progress.setValue(60);
        const { jsPDF } = window.jspdf;
        const doc = new jsPDF();
        
        const splitText = doc.splitTextToSize(text, 180);
        let cursorY = 15;
        for (let i = 0; i < splitText.length; i++) {
          if (cursorY > 280) {
            doc.addPage();
            cursorY = 15;
          }
          doc.text(splitText[i], 15, cursorY);
          cursorY += 7;
        }
        progress.setValue(90);
        doc.save(file.name.replace(/\.docx$/i, ".pdf"));
      }
      progress.setValue(100);
      new ECToast("Conversion finished successfully!", { type: "success" }).show();
      
    } catch (err) {
      console.error(err);
      new ECToast("An error occurred during conversion.", { type: "error" }).show();
    } finally {
      convertBtn.enable();
      setTimeout(() => { progress.element.style.display = "none"; }, 2000);
    }
  });
});