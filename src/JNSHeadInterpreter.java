import br.uff.midiacom.ana.NCLBody;
import br.uff.midiacom.ana.NCLHead;
import br.uff.midiacom.ana.connector.NCLConnectorBase;
import br.uff.midiacom.ana.descriptor.NCLDescriptorBase;
import br.uff.midiacom.ana.region.NCLRegion;
import br.uff.midiacom.ana.reuse.NCLImportBase;
import br.uff.midiacom.ana.reuse.NCLImportNCL;
import br.uff.midiacom.ana.reuse.NCLImportedDocumentBase;
import br.uff.midiacom.ana.rule.NCLRuleBase;
import br.uff.midiacom.ana.transition.NCLTransitionBase;
import br.uff.midiacom.ana.util.ElementList;
import br.uff.midiacom.ana.util.SrcType;
import br.uff.midiacom.ana.util.exception.XMLException;
import java.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class JNSHeadInterpreter
{
  private NCLHead Head;
  public NCLBody body;
  private JNSRegionInterpreter InterpretadorRegiao;
  private JNSDescriptorInterpreter InterpretadorDescritor;
  private JNSTransitionInterpreter InterpretaTransicao;
  private JNSRuleInterpreter InterpretaRegra;
  private JNSConnectorInterpreter InterpretaConnector;

  void setInterpretaRegra(JNSRuleInterpreter InterpretaRegra)
  {
/*  33 */     this.InterpretaRegra = InterpretaRegra;
  }

  JNSRuleInterpreter getInterpretaRegra() {
/*  37 */     return this.InterpretaRegra;
  }

  JNSRegionInterpreter getInterpretadorRegiao() {
/*  41 */     return this.InterpretadorRegiao;
  }

  JNSDescriptorInterpreter getInterpretadorDescritor() {
/*  45 */     return this.InterpretadorDescritor;
  }

  JNSConnectorInterpreter getInterpretaConnector() {
/*  49 */     return this.InterpretaConnector;
  }

  JNSTransitionInterpreter getInterpretaTransicao() {
/*  53 */     return this.InterpretaTransicao;
  }

  JNSHeadInterpreter() throws XMLException {
/*  57 */     this.Head = new NCLHead();
  }
  public NCLHead Interprets(JSONArray jsonHead) throws XMLException, ParseException {
/*  60 */     return Interprets(jsonHead, Boolean.valueOf(false));
  }

  public NCLHead Interprets(JSONArray jsonHead, Boolean imported) throws XMLException, ParseException {
/*  64 */     this.InterpretadorRegiao = new JNSRegionInterpreter(this.Head);
/*  65 */     this.InterpretadorDescritor = new JNSDescriptorInterpreter(this);
/*  66 */     this.InterpretaTransicao = new JNSTransitionInterpreter();
/*  67 */     this.InterpretaConnector = new JNSConnectorInterpreter();
/*  68 */     if (jsonHead == null)
/*  69 */       return this.Head;
/*  70 */     Vector descritores = new Vector();
/*  71 */     this.Head.setDescriptorBase(this.InterpretadorDescritor.getBase());
/*  72 */     Vector headers = new Vector();
/*  73 */     for (int i = 0; i < jsonHead.size(); i++) {
/*  74 */       JSONObject elemento = (JSONObject)jsonHead.get(i);

/*  76 */       if (elemento.containsKey("include")) {
/*  77 */         JSONObject include = (JSONObject)elemento.get("include");
/*  78 */         if (elemento.containsKey("jnsURI")) {
/*  79 */           String file = JsonReader.ReadString((String)include.get("jnsURI"));
/*  80 */           headers.add(JsonReader.parse(file));
        }
        else {
/*  83 */           if (include.containsKey("documentURI")) {
/*  84 */             NCLImportedDocumentBase baseImport = null;
/*  85 */             if (this.Head.getImportedDocumentBase() != null) {
/*  86 */               baseImport = this.Head.getImportedDocumentBase();
            } else {
/*  88 */               baseImport = new NCLImportedDocumentBase();
/*  89 */               this.Head.setImportedDocumentBase(baseImport);
            }
/*  91 */             NCLImportNCL importado = new NCLImportNCL();
/*  92 */             importado.setAlias((String)include.get("alias"));
/*  93 */             importado.setDocumentURI(new SrcType((String)include.get("documentURI")));
/*  94 */             baseImport.addImportNCL(importado);
          }

/*  97 */           if (include.containsKey("descriptorURI")) {
/*  98 */             NCLImportBase importado = new NCLImportBase();
/*  99 */             importado.setDocumentURI(new SrcType((String)include.get("descriptorURI")));
/* 100 */             importado.setAlias((String)include.get("alias"));
/* 101 */             this.InterpretadorDescritor.getBase().addImportBase(importado);
          }
/* 103 */           if (include.containsKey("regionURI")) {
/* 104 */             NCLImportBase importado = new NCLImportBase();
/* 105 */             importado.setDocumentURI(new SrcType((String)include.get("regionURI")));
/* 106 */             importado.setAlias((String)include.get("alias"));
/* 107 */             if (include.containsKey("region")) {
/* 108 */               String region = (String)include.get("region");
/* 109 */               importado.setRegion((NCLRegion)this.InterpretadorRegiao.getBase().findRegion(region));
            }
/* 111 */             include.containsKey("baseId");

/* 114 */             this.InterpretadorRegiao.addImportBase(importado);
          }
/* 116 */           if (include.containsKey("ruleURI")) {
/* 117 */             NCLImportBase importado = new NCLImportBase();
/* 118 */             importado.setDocumentURI(new SrcType((String)include.get("ruleURI")));
/* 119 */             importado.setAlias((String)include.get("alias"));
/* 120 */             this.InterpretaRegra.getBase().addImportBase(importado);
          }
/* 122 */           if (include.containsKey("connectorURI")) {
/* 123 */             NCLImportBase importado = new NCLImportBase();
/* 124 */             importado.setDocumentURI(new SrcType((String)include.get("connectorURI")));
/* 125 */             importado.setAlias((String)include.get("alias"));
/* 126 */             this.InterpretaConnector.getBase().addImportBase(importado);
          }
/* 128 */           if (include.containsKey("transitionURI")) {
/* 129 */             NCLImportBase importado = new NCLImportBase();
/* 130 */             importado.setDocumentURI(new SrcType((String)include.get("transitionURI")));
/* 131 */             importado.setAlias((String)include.get("alias"));
/* 132 */             this.InterpretaTransicao.getBase().addImportBase(importado);
          }
        }
      }
/* 136 */       if (elemento.containsKey("transtion")) {
/* 137 */         if (imported.booleanValue()) {
/* 138 */           if (this.Head.getTransitionBase().getTransitions().get((String)((JSONObject)elemento.get("transtion")).get("id")) == null)
/* 139 */             this.InterpretaTransicao.Add((JSONObject)elemento.get("transtion"));
        }
        else
/* 142 */           this.InterpretaTransicao.Add((JSONObject)elemento.get("transtion"));
      }
/* 144 */       else if ((elemento.containsKey("descriptor")) || (elemento.containsKey("descriptorSwitch"))) {
/* 145 */         if (imported.booleanValue()) {
/* 146 */           if (this.Head.getDescriptorBase().getDescriptor((String)((JSONObject)elemento.get("transtion")).get("id")) == null)
/* 147 */             descritores.add(elemento);
        }
        else
/* 150 */           descritores.add(elemento);
      }
/* 152 */       else if (elemento.containsKey("region")) {
/* 153 */         if (imported.booleanValue()) {
/* 154 */           if (this.InterpretadorRegiao.Bases.findRegion((String)((JSONObject)elemento.get("transtion")).get("id")) == null)
/* 155 */             this.InterpretadorRegiao.Add((JSONObject)elemento.get("region"));
        }
        else
/* 158 */           this.InterpretadorRegiao.Add((JSONObject)elemento.get("region"));
      }
/* 160 */       else if (elemento.containsKey("meta")) {
/* 161 */         this.Head.addMeta(JNSMetaInterpreter.InterMeta((JSONObject)elemento.get("meta")));
      }
/* 163 */       else if (elemento.containsKey("metadata")) {
/* 164 */         this.Head.addMetadata(JNSMetaInterpreter.InterMetadata((String)elemento.get("metadata")));
      }
/* 166 */       else if (elemento.containsKey("rule")) {
/* 167 */         if (imported.booleanValue()) {
/* 168 */           if (this.Head.getRuleBase().getRule((String)((JSONObject)elemento.get("transtion")).get("id")) == null)
/* 169 */             this.InterpretaRegra.Add((JSONObject)elemento.get("rule"));
        }
        else
/* 172 */           this.InterpretaRegra.Add((JSONObject)elemento.get("rule"));
      }
/* 174 */       else if (elemento.containsKey("connector")) {
/* 175 */         if (imported.booleanValue()) {
/* 176 */           if (this.Head.getConnectorBase().getCausalConnector((String)((JSONObject)elemento.get("transtion")).get("id")) == null)
/* 177 */             this.InterpretaConnector.Add((JSONObject)elemento.get("connector"));
        }
        else {
/* 180 */           this.InterpretaConnector.Add((JSONObject)elemento.get("connector"));
        }
      }
    }
int i;
/* 184 */     for (i = 0; i < descritores.size(); i++) {
/* 185 */       JSONObject elemento = (JSONObject)descritores.get(i);
/* 186 */       if (elemento.containsKey("descriptor")) {
/* 187 */         this.InterpretadorDescritor.Add((JSONObject)elemento.get("descriptor"));
      }
/* 189 */       else if (elemento.containsKey("descriptorSwitch")) {
/* 190 */         this.InterpretadorDescritor.AddSwitc((JSONObject)elemento.get("descriptorSwitch"));
      }
    }

/* 194 */     for (i = 0; i < headers.size(); i++) {
/* 195 */       Interprets((JSONArray)headers.get(i), Boolean.valueOf(true));
    }

/* 198 */     if (this.InterpretaTransicao.getBase().hasTransition())
/* 199 */       this.Head.setTransitionBase(this.InterpretaTransicao.getBase());
/* 200 */     this.InterpretadorRegiao.AdicionaRegioesFaltantes();
/* 201 */     return this.Head;
  }

  public void InsertBases()
    throws XMLException
  {
     this.InterpretadorDescritor.ReInterprets();
     if (this.InterpretadorDescritor.getBase().hasDescriptor())
       this.Head.setDescriptorBase(this.InterpretadorDescritor.getBase());
     if (this.InterpretaRegra.getBase().hasRule())
       this.Head.setRuleBase(this.InterpretaRegra.getBase());
     if (this.InterpretaConnector.getBase().hasCausalConnector())
       this.Head.setConnectorBase(this.InterpretaConnector.getBase());
  }
}